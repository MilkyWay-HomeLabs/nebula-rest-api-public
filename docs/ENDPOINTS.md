# Endpoints Documentation

This document describes the currently implemented REST API endpoints exposed by the Nebula REST API.

## Base Paths

The application currently uses two main HTTP namespaces:

- business API: `/api/v1`
- operational endpoints (Actuator): `/api/actuator`

This document focuses on the business API under `/api/v1`. Operational endpoints are documented separately in `docs/METRICS.md`.

## API Style

The API is organized according to the hexagonal architecture used in the codebase:

- inbound HTTP entry points live in `adapter.in.rest`
- DTO mapping is delegated to REST mappers and assemblers
- application logic is executed through `port.in` use cases
- infrastructure and external calls are delegated to `adapter.out`

Several endpoints produce `application/hal+json`, especially where HATEOAS-aware response models are returned.

## Effective Access Model

The current API uses four practical access patterns:

1. **Public read endpoints** — no authentication required
2. **Public write endpoints** — no authentication required, but CSRF still applies for browser clients
3. **Cookie-based authenticated endpoints** — expect `accessToken` or `refreshToken` cookies
4. **Header-based admin endpoints** — expect the `Authorization` header and an admin-role check

Spring Security now enforces the public/private route split directly in `AppConfig.securityFilterChain(...)`, and undocumented routes fall back to `denyAll()`.

## CSRF Requirements

CSRF protection is currently enabled.

For browser-based clients, unsafe methods such as `POST`, `PATCH`, `PUT`, and `DELETE` must include:

- `XSRF-TOKEN` cookie
- `X-XSRF-TOKEN` header with the same token value

This applies even to public mutation endpoints such as:

- `POST /api/v1/account/token`
- `POST /api/v1/account/register`
- `PATCH /api/v1/account/confirm`
- `PATCH /api/v1/account/unlock/{id}`
- `PATCH /api/v1/account/reset-password/{email}`
- `POST /api/v1/token/refresh/access`

## Endpoint Catalog

### Health

| Method | Path              | Access | Description                    |
|:-------|:------------------|:-------|:-------------------------------|
| `GET`  | `/api/v1/hello`   | Public | Liveness-style health response |
| `GET`  | `/api/v1/version` | Public | Returns application version    |

### Account

| Method  | Path                                     | Access                        | Description                              |
|:--------|:-----------------------------------------|:------------------------------|:-----------------------------------------|
| `POST`  | `/api/v1/account/register`               | Public + CSRF                 | Registers a new account                  |
| `PATCH` | `/api/v1/account/confirm`                | Public + CSRF                 | Confirms a registration token            |
| `PATCH` | `/api/v1/account/unlock/{id}`            | Public + CSRF                 | Requests account unlock flow             |
| `PATCH` | `/api/v1/account/reset-password/{email}` | Public + CSRF                 | Starts reset-password flow               |
| `POST`  | `/api/v1/account/token`                  | Public + CSRF                 | Performs login and returns token cookies |
| `POST`  | `/api/v1/account/change-password`        | `accessToken` cookie + CSRF   | Changes password for the token owner     |

### Token

| Method | Path                           | Access                      | Description                                    |
|:-------|:-------------------------------|:----------------------------|:-----------------------------------------------|
| `GET`  | `/api/v1/token`                | `accessToken` cookie        | Returns token metadata                         |
| `GET`  | `/api/v1/token/valid`          | `accessToken` cookie        | Returns whether token is valid                 |
| `GET`  | `/api/v1/token/roles`          | `accessToken` cookie        | Returns roles from token                       |
| `GET`  | `/api/v1/token/email`          | `accessToken` cookie        | Returns email from token                       |
| `GET`  | `/api/v1/token/id`             | `accessToken` cookie        | Returns user id from token                     |
| `POST` | `/api/v1/token/refresh/access` | `refreshToken` cookie + CSRF| Refreshes access token and returns new cookies |

### User

| Method  | Path                     | Access                      | Description                         |
|:--------|:-------------------------|:----------------------------|:------------------------------------|
| `GET`   | `/api/v1/users`          | `accessToken` cookie        | Returns current user profile        |
| `PATCH` | `/api/v1/users/profile`  | `accessToken` cookie + CSRF | Updates profile of the token owner  |
| `PUT`   | `/api/v1/users/settings` | `accessToken` cookie + CSRF | Updates settings of the token owner |

### User Achievements

| Method | Path                                                  | Access               | Description                                            |
|:-------|:------------------------------------------------------|:---------------------|:-------------------------------------------------------|
| `GET`  | `/api/v1/users/achievements/{userId}/{achievementId}` | `accessToken` cookie | Returns a single achievement for a user                |
| `GET`  | `/api/v1/users/achievements/list`                     | `accessToken` cookie | Returns the full achievement list for the current user |
| `GET`  | `/api/v1/users/achievements`                          | `accessToken` cookie | Returns paged and filtered achievements                |

### Image

| Method | Path            | Access                      | Description               |
|:-------|:----------------|:----------------------------|:--------------------------|
| `POST` | `/api/v1/image` | `accessToken` cookie + CSRF | Uploads user avatar image |

### Games

| Method   | Path                 | Access                               | Description                    |
|:---------|:---------------------|:-------------------------------------|:-------------------------------|
| `GET`    | `/api/v1/games/{id}` | Public                               | Returns a single game          |
| `GET`    | `/api/v1/games`      | Public                               | Returns paged games            |
| `GET`    | `/api/v1/games/enabled` | Public                            | Returns enabled games only     |
| `POST`   | `/api/v1/games`      | `Authorization` header + CSRF + admin role | Creates a game          |
| `PUT`    | `/api/v1/games/{id}` | `Authorization` header + CSRF + admin role | Updates a game          |
| `DELETE` | `/api/v1/games/{id}` | `Authorization` header + CSRF + admin role | Deletes a game          |

### Reference Data

#### Genders

| Method | Path                  | Access | Description             |
|:-------|:----------------------|:-------|:------------------------|
| `GET`  | `/api/v1/genders/{id}`| Public | Returns a single gender |
| `GET`  | `/api/v1/genders`     | Public | Returns all genders     |

#### Nationalities

| Method | Path                        | Access | Description                  |
|:-------|:----------------------------|:-------|:-----------------------------|
| `GET`  | `/api/v1/nationalities/{id}`| Public | Returns a single nationality |
| `GET`  | `/api/v1/nationalities`     | Public | Returns all nationalities    |

#### Themes

| Method | Path                 | Access | Description            |
|:-------|:---------------------|:-------|:-----------------------|
| `GET`  | `/api/v1/themes/{id}`| Public | Returns a single theme |
| `GET`  | `/api/v1/themes`     | Public | Returns all themes     |

## Request and Response Notes

### Account login and refresh flows

- `POST /api/v1/account/token` returns a body with token-related user data and sets:
  - `accessToken`
  - `refreshToken`
- `POST /api/v1/token/refresh/access` expects `refreshToken` cookie and returns renewed cookies
- browser-based clients should bootstrap CSRF before the first unsafe request

### User endpoints

- `GET /api/v1/users` returns `NebulaUserResponse`
- `PATCH /api/v1/users/profile` accepts `ProfileUpdateRequest`
- `PUT /api/v1/users/settings` accepts `UserSettingsRequest`
- profile/settings mutations additionally validate that the token belongs to the same user as the request payload

### Game admin endpoints

`POST`, `PUT`, and `DELETE` for `/api/v1/games` rely on:

- authenticated request context
- `Authorization` header input in the controller
- a dedicated admin-role check use case

### Actuator and documentation endpoints

The following paths are relevant outside the business API catalog:

- Swagger UI: `/nebula/swagger-ui.html`
- OpenAPI docs: `/nebula/v3/api-docs`
- Swagger config: `/nebula/v3/api-docs/swagger-config`
- public Actuator: `/api/actuator/health`, `/api/actuator/info`
- authenticated Actuator: `/api/actuator/prometheus`, remaining `/api/actuator/**`

## Error Handling Conventions

The application mixes two styles of failure handling:

1. **SecurityFilterChain responses**
   - `401 UNAUTHORIZED` for missing or invalid authentication on protected routes
   - `403 FORBIDDEN` for CSRF failures and authenticated access-denied outcomes
2. **Controller-level status returns and central exception handling**
   - `TokenExpiredException` -> `401 UNAUTHORIZED`
   - `IllegalArgumentException`, `MissingHeaderException` -> `400 BAD_REQUEST`
   - domain not found exceptions -> `404 NOT_FOUND`
   - `GameAlreadyExistsException` -> `409 CONFLICT`
   - `HttpRequestException` -> `502 BAD_GATEWAY`

Because several controllers still translate business and ownership failures directly, not every failure path is centralized in one place.

## Practical Reading Order

If you want to understand the HTTP API from the codebase, the best order is:

1. `account` and `token` for authentication/token lifecycle
2. `user` and `userachievement` for owner-scoped functionality
3. `game` for public + admin-protected operations
4. reference data controllers (`gender`, `nationality`, `theme`)
5. `health` and `image`
