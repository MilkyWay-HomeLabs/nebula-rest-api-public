# Authentication & Token Integration Guide

This document explains how **client applications** (web front-ends, mobile apps,
backend services) authenticate against the **Nebula REST API**, how access and
refresh tokens work, and how to keep a session alive.

Nebula is **not** the identity provider. It plays two roles:

1. **Authentication gateway (BFF).** Login and access-token refresh are *proxied*
   to the external **Andromeda Authorization Server**, which actually issues,
   rotates, and revokes the tokens.
2. **Resource server.** Nebula validates the JWT issued by Andromeda on every
   protected call and populates the security context from its claims.

```
  client ──▶ Nebula /account/token ──▶ Andromeda /auth/login ──▶ tokens
     │                                                             │
     │◀──────────── accessToken + refreshToken cookies ◀───────────┘
     ▼
  client ──▶ Nebula protected endpoint (accessToken cookie)   [validated locally]
     │
     ▼  (access expires → 401)
  client ──▶ Nebula /token/refresh/access ──▶ Andromeda /auth/refresh-access
     │                                                             │
     │◀──────────── new accessToken + refreshToken cookies ◀───────┘
     ▼  (refresh expires / session ends)
  log in again
```

> **See also:** [`SECURITY.md`](./SECURITY.md) for the security model and
> [`ENDPOINTS.md`](./ENDPOINTS.md) for the full endpoint catalogue.

---

## Table of Contents

1. [Overview](#1-overview)
2. [What is different from Andromeda's own API](#2-what-is-different-from-andromedas-own-api)
3. [Token model](#3-token-model)
4. [Cookies](#4-cookies)
5. [Login](#5-login)
6. [Calling protected endpoints](#6-calling-protected-endpoints)
7. [Refreshing the access token](#7-refreshing-the-access-token)
8. [Ending a session](#8-ending-a-session)
9. [Admin (header-based) access](#9-admin-header-based-access)
10. [CORS & CSRF for browser clients](#10-cors--csrf-for-browser-clients)
11. [Error reference](#11-error-reference)
12. [End-to-end example](#12-end-to-end-example)

---

## 1. Overview

Two JWTs are issued (by Andromeda) on successful login:

| Token             | Purpose                                                      | Lifetime        |
|-------------------|--------------------------------------------------------------|-----------------|
| **Access token**  | Authorizes calls to protected Nebula endpoints.              | Short-lived     |
| **Refresh token** | Used only to obtain a new access token (and a new refresh token). | Longer-lived, capped by the session |

Both tokens are delivered as **`HttpOnly` cookies** (`accessToken`,
`refreshToken`), never in the response body. Clients do not read the token
values — the browser (or an HTTP client with a cookie jar) sends them back
automatically.

All authentication traffic goes through the business API base path **`/api/v1`**.
The relevant endpoints are:

| Purpose             | Nebula endpoint                       | Proxied to (Andromeda)      |
|---------------------|---------------------------------------|-----------------------------|
| Login               | `POST /api/v1/account/token`          | `POST /api/v1/auth/login`   |
| Refresh access      | `POST /api/v1/token/refresh/access`   | `POST /api/v1/auth/refresh-access` |
| Change password     | `POST /api/v1/account/change-password`| `POST /api/v1/account/change-password` |
| Register / confirm / unlock / reset | `/api/v1/account/**`  | `/api/v1/public/account/**` |

Token **validation** on protected endpoints happens **inside Nebula** — it does
not call Andromeda per request. Nebula verifies the JWT's HS256 signature with a
secret shared with Andromeda (`app.jwt.secret` / `APP_JWT_SECRET`) and checks the
expiry.

---

## 2. What is different from Andromeda's own API

If you have integrated directly with Andromeda before, note these Nebula-specific
differences:

- **No `X-Requesting-App` header required from you.** Andromeda requires a
  whitelisted `X-Requesting-App` header, but Nebula adds that header *itself*
  when it calls Andromeda (from `HEADER_FOR_AUTH_SERVER`). Clients calling Nebula
  **do not** send it.
- **Different paths.** Login is `POST /api/v1/account/token` (not
  `/api/v1/auth/login`); refresh is `POST /api/v1/token/refresh/access` (not
  `/api/v1/auth/refresh-access`).
- **Login request field is `email`.** The login body uses `email` (which may hold
  an email *or* a username — Andromeda resolves it) and `password`.
- **CSRF applies to login and refresh.** Unlike Andromeda, Nebula does **not**
  exempt login/refresh from CSRF. Browser clients must bootstrap a CSRF token
  first (see [section 10](#10-cors--csrf-for-browser-clients)).
- **No logout endpoint.** Nebula does not expose `logout` / `logout-all`. A
  session ends by token expiry (see [section 8](#8-ending-a-session)).
- **Refresh cookies are re-issued with `SameSite=Strict`.** On refresh, Nebula
  rebuilds the cookies (`HttpOnly`, `Secure`, `SameSite=Strict`, `Path=/`)
  instead of forwarding Andromeda's attributes verbatim (login cookies keep
  `SameSite=None`).

---

## 3. Token model

Both tokens are **HS256-signed JWTs** issued by Andromeda (`iss: AndromedaAuthApi`).
Nebula reads the following from a valid access token:

| Value    | Source claim | Notes                                                        |
|----------|--------------|--------------------------------------------------------------|
| User ID  | `sub`        | Subject is encoded as `"<userId>,<email>"`; Nebula splits on `,`. |
| Email    | `sub` / `email` | Taken from the subject's second part, or the `email` claim. |
| Roles    | `roles`      | List of `{ id, name }`; names such as `ROLE_USER`, `ROLE_ADMIN`. |
| Expiry   | `exp`        | Expired tokens are rejected.                                 |

Andromeda additionally embeds `jti`, `version`, `revoked`, `iat` and (on refresh
tokens) `session_exp` for server-side rotation and revocation. Those are managed
by Andromeda; Nebula relies on the signature + expiry for authorization and
delegates rotation/revocation to Andromeda during refresh.

The signing secret is configured via:

| Property         | Env var          | Purpose                                  |
|------------------|------------------|------------------------------------------|
| `app.jwt.secret` | `APP_JWT_SECRET` | HS256 (≥256-bit) key, shared with Andromeda. |

---

## 4. Cookies

Cookie names:

- `accessToken` — the access JWT (used to authorize protected calls).
- `refreshToken` — the refresh JWT (used only to refresh).

**On login**, Nebula forwards Andromeda's `Set-Cookie` headers. If Andromeda does
not already set `SameSite`, Nebula appends `SameSite=None; Secure; Partitioned`:

| Attribute  | Login cookies                          |
|------------|----------------------------------------|
| `HttpOnly` | `true`                                 |
| `Secure`   | `true`                                 |
| `SameSite` | `None` (cross-site, requires `Secure`) |
| `Partitioned` | added when `SameSite` is injected   |

**On refresh**, Nebula re-issues both cookies with its own attributes:

| Attribute  | Refresh cookies |
|------------|-----------------|
| `HttpOnly` | `true`          |
| `Secure`   | `true`          |
| `SameSite` | `Strict`        |
| `Path`     | `/`             |

Because the cookies are `Secure`, integration requires **HTTPS**, and browser
clients must send credentials (see [section 10](#10-cors--csrf-for-browser-clients)).

---

## 5. Login

- **URL:** `POST /api/v1/account/token`
- **Auth:** none (public). CSRF token required for browser clients.
- **Body:** `email` + `password`.

```http
POST /api/v1/account/token
Content-Type: application/json
X-XSRF-TOKEN: <token from XSRF-TOKEN cookie>   # browser clients only

{
  "email": "user@example.com",
  "password": "s3cret-password"
}
```

Field constraints:

| Field      | Rules                                         |
|------------|-----------------------------------------------|
| `email`    | 5–50 chars. Email **or** username (Andromeda resolves it). |
| `password` | 5–64 chars, not blank.                        |

**Success — `200 OK`** — sets `accessToken` + `refreshToken` cookies, body:

```json
{
  "username": "user",
  "email": "user@example.com"
}
```

**Failure** — invalid credentials are rejected upstream; Nebula surfaces the
auth-service failure (see [section 11](#11-error-reference)).

---

## 6. Calling protected endpoints

For any protected endpoint present the access token in **either** way:

1. **Cookie (default):** send the `accessToken` cookie — automatic for browsers
   and cookie-aware HTTP clients.
2. **Authorization header:** `Authorization: Bearer <access-token>` — useful for
   service-to-service calls. (A bare token without the `Bearer ` prefix is also
   accepted.)

```http
GET /api/v1/users
Cookie: accessToken=<jwt>
```

The `JwtAuthenticationFilter` validates the signature and expiry, then populates
the security context with the user ID, email, and roles from the token. Role-
restricted endpoints require the matching `ROLE_*` in the `roles` claim.

If the access token is missing, malformed, or expired, the request proceeds
**unauthenticated** and protected endpoints respond `401 Unauthorized`. That is
the signal to run the refresh flow.

> Some ownership-sensitive endpoints (e.g. `change-password`, profile/settings
> updates) additionally check that the token's user ID matches the target user
> and answer `403 Forbidden` on mismatch.

---

## 7. Refreshing the access token

When the access token expires, exchange the refresh token for a fresh pair.
**No request body is needed** — the refresh token travels in its cookie.

- **URL:** `POST /api/v1/token/refresh/access`
- **Auth:** the `refreshToken` cookie (public endpoint; access token not needed).
  CSRF token required for browser clients.

```http
POST /api/v1/token/refresh/access
Cookie: refreshToken=<refresh-jwt>
X-XSRF-TOKEN: <token from XSRF-TOKEN cookie>   # browser clients only
```

**Success — `200 OK`** — sets **new** `accessToken` **and** `refreshToken`
cookies (rotation), body:

```json
{
  "success": true,
  "type": "ACCESS_REFRESHED"
}
```

**Failure — `401 Unauthorized`** — the refresh token is missing, expired,
invalid, already used, or the session has ended. Re-authenticate.

### Rotation & replay detection (in Andromeda)

Nebula forwards the refresh token to Andromeda, which enforces **single-use
rotation**: the old refresh token is revoked and a new access + refresh pair is
issued, carrying forward the original session cap (`session_exp`) so the session
cannot be extended past its absolute deadline. Reusing a consumed refresh token
is detected as a replay and denied.

Because a structurally valid refresh token can still be rejected once rotated or
revoked, treat any `401` from this endpoint as "session over → log in again".

### `401` vs `502`

Nebula distinguishes rejection from an outage:

- Andromeda answers `401` → Nebula returns **`401 UNAUTHORIZED`** (re-authenticate).
- Andromeda is unreachable or returns `5xx` → Nebula returns **`502 BAD_GATEWAY`**
  (transient; safe to retry once before falling back to re-login).

---

## 8. Ending a session

Nebula does **not** expose a logout endpoint. A session ends when:

- the **access token expires** and the client cannot (or chooses not to) refresh;
- the **refresh token / session cap expires**, so refresh returns `401`.

To end a session on the client side, discard the cookies (browser: clear them /
let them expire; server client: drop them from the cookie jar). Global,
server-side revocation (`logout-all`, token-version bump) is an Andromeda
capability and is not proxied through Nebula.

---

## 9. Admin (header-based) access

A few endpoints require an authenticated **admin** caller and read the token from
the `Authorization` header:

| Method   | Path                 | Requirement                          |
|----------|----------------------|--------------------------------------|
| `POST`   | `/api/v1/games`      | `Authorization: Bearer <jwt>` + admin role + CSRF |
| `PUT`    | `/api/v1/games/{id}` | `Authorization: Bearer <jwt>` + admin role + CSRF |
| `DELETE` | `/api/v1/games/{id}` | `Authorization: Bearer <jwt>` + admin role + CSRF |

The admin role check is enforced in the controller/use case in addition to the
authenticated route rule.

---

## 10. CORS & CSRF for browser clients

**CORS.** Nebula allows credentialed cross-origin requests from the `*.milkyway`
family of origin patterns (configurable via `cors.allowed-origin-patterns` /
`CORS_ALLOWED_ORIGIN_PATTERNS`). Browser clients **must** send credentials so
cookies flow both ways:

```js
fetch("https://<nebula-host>/api/v1/account/token", {
  method: "POST",
  credentials: "include",              // required — send/receive cookies
  headers: {
    "Content-Type": "application/json",
    "X-XSRF-TOKEN": xsrfToken,          // required — see CSRF below
  },
  body: JSON.stringify({ email, password }),
});
```

**CSRF.** CSRF protection is **enabled** for all unsafe methods (`POST`, `PUT`,
`PATCH`, `DELETE`) — including login and refresh. The token is delivered in a
JS-readable `XSRF-TOKEN` cookie and must be echoed back in the `X-XSRF-TOKEN`
request header.

Browser flow:

1. Make a safe request first (any `GET`, e.g. `GET /api/v1/version`) to obtain
   the `XSRF-TOKEN` cookie.
2. Read the `XSRF-TOKEN` cookie value.
3. Send it as the `X-XSRF-TOKEN` header on every subsequent unsafe request.

Non-browser clients that do not carry cookies between requests generally need to
perform the same bootstrap (obtain `XSRF-TOKEN`, echo it back) because CSRF is
not path-exempted.

---

## 11. Error reference

| Status | Where              | Meaning / action                                                       |
|:------:|--------------------|------------------------------------------------------------------------|
| `401`  | protected call     | Access token missing/expired/invalid. Run refresh, then retry.         |
| `401`  | refresh-access     | Refresh token missing/expired/invalid/replayed, or session ended. Re-login. |
| `403`  | CSRF               | Missing/invalid CSRF token on an unsafe method. Bootstrap `XSRF-TOKEN`. |
| `403`  | ownership / admin  | Token does not own the resource, or lacks the required role.           |
| `502`  | login / refresh    | Auth service unreachable or upstream `5xx`. Retry once, then re-login. |

Recommended client behavior: on a `401` from a protected endpoint, call
`refresh-access` once and replay the original request; if refresh also returns
`401`, clear local state and redirect to login.

---

## 12. End-to-end example

```http
### 0. (Browser only) Bootstrap CSRF
GET /api/v1/version
# → 200 OK, Set-Cookie: XSRF-TOKEN=...

### 1. Log in
POST /api/v1/account/token
Content-Type: application/json
X-XSRF-TOKEN: {{xsrf}}

{ "email": "user@example.com", "password": "s3cret-password" }
# → 200 OK, Set-Cookie: accessToken=...; refreshToken=...
#   { "username": "user", "email": "user@example.com" }

### 2. Call a protected endpoint (cookie sent automatically)
GET /api/v1/users
Cookie: accessToken={{accessToken}}
# → 200 OK  (or 401 if the access token has expired → go to step 3)

### 3. Refresh when the access token expires
POST /api/v1/token/refresh/access
Cookie: refreshToken={{refreshToken}}
X-XSRF-TOKEN: {{xsrf}}
# → 200 OK, new accessToken + refreshToken cookies
#   { "success": true, "type": "ACCESS_REFRESHED" }
# → 401 means the session ended; go back to step 1.
```
