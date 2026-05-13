# Security Documentation

This document describes the current security model implemented in the Nebula REST API.

> [!WARNING]
> The current `5.4.0-PUBLIC-PREVIEW` release is **not production-hardened**. It is suitable for source publication and
> preview environments, but it should not be treated as a finished Internet-facing security configuration without
> additional infrastructure hardening, CSRF flow validation across clients, actuator access review, and deployment review.

## Current Security Model

The project uses a layered security model:

- Spring Security is enabled and actively enforces public/private route boundaries.
- JWT-based identity is used for authenticated operations.
- Cookie-based authentication is the primary mechanism for end-user flows.
- The `Authorization` header is used for selected administrative operations.
- A custom `JwtAuthenticationFilter` populates `SecurityContextHolder` before authorization checks.
- Controller-level ownership and admin checks still complement the route-level rules.

## Verified Spring Security Configuration

The current `SecurityFilterChain` in `AppConfig` has the following important properties:

- CORS is enabled.
- CSRF is enabled through `CookieCsrfTokenRepository.withHttpOnlyFalse()`.
- `JwtAuthenticationFilter` is registered before `UsernamePasswordAuthenticationFilter`.
- JSON `401 UNAUTHORIZED` responses are returned through a custom `authenticationEntryPoint`.
- JSON `403 FORBIDDEN` responses are returned through a custom `accessDeniedHandler`.
- Undocumented routes fall back to `anyRequest().denyAll()`.

### Public routes

The current allowlist includes:

- `GET /api/v1/hello`
- `GET /api/v1/version`
- `POST /api/v1/account/register`
- `PATCH /api/v1/account/confirm`
- `PATCH /api/v1/account/unlock/**`
- `PATCH /api/v1/account/reset-password/**`
- `POST /api/v1/account/token`
- `POST /api/v1/token/refresh/access`
- `GET /api/v1/games`
- `GET /api/v1/games/*`
- `GET /api/v1/games/enabled`
- `GET /api/v1/genders`
- `GET /api/v1/genders/*`
- `GET /api/v1/nationalities`
- `GET /api/v1/nationalities/*`
- `GET /api/v1/themes`
- `GET /api/v1/themes/*`
- `/nebula/**` for OpenAPI and Swagger resources
- `GET /api/actuator/health/**`
- `GET /api/actuator/info`

### Authenticated routes

The current authenticated route set includes:

- `/api/v1/account/change-password`
- `/api/v1/token/**`
- `/api/v1/users/**`
- `/api/v1/image`
- `POST /api/v1/games`
- `PUT /api/v1/games/**`
- `DELETE /api/v1/games/**`
- `/api/actuator/prometheus`
- all remaining `/api/actuator/**`

### Implication

Unlike the earlier public-preview state, the application now uses Spring Security route matchers as the first access-control layer. Controllers still perform additional business checks such as owner validation and admin-role validation, but request authentication is no longer left to controller logic alone.

## JWT Authentication Filter

`JwtAuthenticationFilter` is the bridge between incoming tokens and Spring Security authentication.

### Token resolution order

The filter resolves credentials in this order:

1. `accessToken` cookie
2. `Authorization` header
   - supports `Bearer <token>`
   - also accepts a raw header value if no `Bearer ` prefix is present

### Successful authentication outcome

If the token is valid, the filter:

- extracts `userId`
- extracts `email`
- extracts authorities from token roles
- creates `UsernamePasswordAuthenticationToken`
- stores it in `SecurityContextHolder`

If the token is missing, blank, invalid, or throws during parsing, the filter leaves the security context empty and the normal authorization flow continues.

## Authentication Inputs Used by the API

### 1. Cookie-based tokens

The following cookies are used by the REST API:

- `accessToken`
- `refreshToken`
- `XSRF-TOKEN`

`accessToken` is used for owner-scoped and token-inspection endpoints such as:

- `/api/v1/users`
- `/api/v1/users/profile`
- `/api/v1/users/settings`
- `/api/v1/users/achievements/*`
- `/api/v1/token/*`
- `/api/v1/image`
- `/api/v1/account/change-password`

`refreshToken` is used for:

- `/api/v1/token/refresh/access`

### 2. Authorization header

Administrative game mutation endpoints still rely on the `Authorization` header:

- `POST /api/v1/games`
- `PUT /api/v1/games/{id}`
- `DELETE /api/v1/games/{id}`

Those routes are also authenticated at the filter-chain level, and the controller applies an additional admin-role check through `CheckAdminRoleUseCase`.

## CSRF

CSRF protection is currently enabled.

### Verified configuration

The application uses:

```java
CookieCsrfTokenRepository.withHttpOnlyFalse()
```

and:

```java
new CsrfTokenRequestAttributeHandler()
```

### Practical client requirement

Unsafe browser requests such as `POST`, `PATCH`, `PUT`, and `DELETE` must include a valid CSRF token.

In practice, SPA/browser clients should:

1. obtain the `XSRF-TOKEN` cookie from a safe request
2. send the same value back in the `X-XSRF-TOKEN` header for unsafe requests

If the token is missing or invalid, the configured `accessDeniedHandler` returns:

```json
{"message":"CSRF token is missing or invalid.","error":"FORBIDDEN"}
```

## JWT Handling

The JWT handling flow is split into dedicated security components.

### `TokenProvider`

The `TokenProvider` abstraction provides:

- `getTokenData(String token)`
- `isValid(String token)`
- `isValid(String token, long userId)`
- `getUserId(String token)`
- `getEmail(String token)`
- `getRoles(String token)`

### `TokenProviderImpl`

The current implementation:

- returns `false` for `null` or empty token input
- throws `TokenExpiredException` for expired tokens
- validates owner-bound operations using `isValid(token, userId)`
- delegates JWT parsing to `JwtTokenUtil`

### `JwtTokenUtil`

`JwtTokenUtil` is responsible for:

- parsing signed JWT claims
- checking expiration
- extracting user id from `subject`
- extracting email from either `subject` or the dedicated `email` claim
- extracting roles from the `roles` claim
- constructing `TokenData`

The token secret is loaded from:

```properties
app.jwt.secret=${APP_JWT_SECRET}
```

## Identity, Ownership, and Role Rules

The API uses several authorization patterns after the route-level security rules allow a request through.

### Owner-only flows

The following operations validate that the acting token belongs to the target user:

- `PATCH /api/v1/users/profile`
- `PUT /api/v1/users/settings`
- `POST /api/v1/account/change-password`
- `GET /api/v1/users/achievements/{userId}/{achievementId}`

Typical controller behavior:

- missing or unauthenticated request -> blocked by Spring Security with `401`
- authenticated but wrong owner -> rejected by controller logic with `401` or `403`, depending on endpoint semantics

### Token-derived current-user flows

Some endpoints only require a valid authenticated token and derive the current user directly from it:

- `GET /api/v1/users`
- `GET /api/v1/users/achievements/list`
- `GET /api/v1/users/achievements`
- `POST /api/v1/image`
- `GET /api/v1/token`
- `GET /api/v1/token/valid`
- `GET /api/v1/token/roles`
- `GET /api/v1/token/email`
- `GET /api/v1/token/id`

### Admin flows

Game write operations use a dedicated admin-role validation path on top of authentication:

- header-based token input in the controller method
- `CheckAdminRoleUseCase`
- `403 FORBIDDEN` when the admin role is missing

## Login and Refresh Flow

### Login

`POST /api/v1/account/token`

Request body:

- email
- password

Result:

- response body with token-related user information
- `Set-Cookie` headers for:
  - `accessToken`
  - `refreshToken`

Because CSRF is enabled, browser clients should perform the login flow only after obtaining a valid CSRF token cookie.

### Refresh access token

`POST /api/v1/token/refresh/access`

Input:

- `refreshToken` cookie
- valid CSRF token for browser-based clients

Behavior:

- validates refresh token
- invokes the dedicated refresh access use case
- returns renewed `accessToken` and `refreshToken` cookies
- sanitizes cookie values before adding them to headers

## External Authentication Service

The account and token lifecycle is partly integrated with an external authentication service through `HttpAuthClient`.

That client is responsible for operations such as:

- registration
- confirmation
- unlock
- reset password
- change password
- login token generation
- refresh access
- account role lookup

This means that security behavior in the REST API is a combination of:

- Spring Security route enforcement
- local JWT validation and role extraction
- remote auth-service interaction
- controller-level ownership and role checks

## Error Handling and Security-Relevant Status Codes

### SecurityFilterChain-level responses

- `401 UNAUTHORIZED`
  - returned when an authenticated route is accessed without a valid authenticated principal
  - JSON body: `{"message":"Authentication is required.","error":"UNAUTHORIZED"}`
- `403 FORBIDDEN`
  - returned for CSRF failures and access-denied situations after authentication
  - JSON body for CSRF failure: `{"message":"CSRF token is missing or invalid.","error":"FORBIDDEN"}`
  - JSON body for generic access denial: `{"message":"Access denied.","error":"FORBIDDEN"}`

### Controller and exception-mapper responses still in use

The codebase still uses additional direct controller responses and exception mapping for:

- `400 BAD_REQUEST`
- `401 UNAUTHORIZED`
- `403 FORBIDDEN`
- `409 CONFLICT`
- `502 BAD_GATEWAY`

Representative examples:

- `TokenExpiredException` -> handled through `GlobalExceptionHandler`
- selected owner mismatches -> direct controller rejection
- remote auth-service failures -> `HttpRequestException` -> `502 BAD_GATEWAY`

## CORS

The verified CORS configuration:

- loads allowed origin patterns from `cors.allowed-origin-patterns`
- trims empty values
- allows credentials
- allows methods: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS`
- allows headers: `*`

The default public-preview allowlist currently includes these origin patterns:

- `https://milkyway.test`
- `https://*.milkyway.test`
- `https://milkyway.inet`
- `https://*.milkyway.inet`
- `https://*.milkyway`
- `https://*.test.milkyway`
- `https://*.dev.milkyway`

## Security Posture Summary

### Current strengths

- authenticated/private routes are enforced in Spring Security
- JWT authentication is integrated into `SecurityContextHolder`
- CSRF protection is enabled for unsafe requests
- owner checks remain explicit for sensitive user operations
- admin-role checks remain explicit for game mutations
- actuator endpoints are not fully public

### Current limitations

- the public preview still requires deployment-specific hardening and end-to-end review
- controller-level authorization logic still exists alongside filter-chain rules
- browser clients must correctly implement CSRF bootstrap and header forwarding
- actuator exposure and scraper access should be reviewed together with `docs/METRICS.md`
- public preview should still be treated as a development/review baseline rather than a production-ready security profile

## Related Documents

- `docs/ENDPOINTS.md`
- `docs/METRICS.md`
- `docs/ARCHITECTURE.md`
