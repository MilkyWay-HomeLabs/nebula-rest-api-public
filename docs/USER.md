# User Area Documentation

This document describes the `user` bounded area from the REST API perspective and explains how it maps to the underlying hexagonal architecture.

## Scope

The primary user-facing controller is:

- `UserController` -> `/api/v1/users`

Closely related user-facing functionality also exists in:

- `UserAchievementController` -> `/api/v1/users/achievements`
- `ImageController` -> `/api/v1/image`
- `TokenController` -> `/api/v1/token` (identity and token metadata)

## Architectural Position

The `user` area follows the same layered structure as the rest of the project:

- inbound REST adapter: `user.adapter.in.rest`
- request/response mapping: `user.adapter.in.rest.mapper`
- use cases: `user.application.port.in`
- application service: `user.application.service`
- outbound persistence adapter: `user.adapter.out.persistence`
- domain model and builders: `user.domain.*`

Typical flow:

1. `UserController` receives HTTP request
2. `TokenProvider` validates ownership
3. `UserRestMapper` converts request DTOs to commands/domain structures
4. a `port.in` use case is invoked
5. `UserService` delegates to `UserRepositoryPort`
6. outbound persistence adapter loads or persists state
7. result is mapped back to response DTOs

## User Endpoints

Base path:

- `/api/v1/users`

### 1. Get current user

**Endpoint**

- `GET /api/v1/users`

**Authentication**

- requires `accessToken` cookie

**Behavior**

- validates token with `tokenProvider.isValid(accessToken)`
- extracts current user id from the token
- returns the profile of the authenticated user
- returns `403 FORBIDDEN` when token is invalid

**Response type**

- `NebulaUserResponse`

**Returned fields**

- `id`
- `login`
- `email`
- `firstName`
- `lastName`
- `age`
- `birthDate`
- `gender`
- `nationality`
- `settings`
- `games`
- `achievements`

### 2. Update user profile

**Endpoint**

- `PATCH /api/v1/users/profile`

**Authentication**

- requires `accessToken` cookie
- token must belong to the same `userId` as provided in the payload

**Request body**

- `ProfileUpdateRequest`

Fields:

- `userId` (required)
- `firstName`
- `lastName`
- `birthdate`
- `nationalityId`
- `genderId`

**Behavior**

- validates ownership with `tokenProvider.isValid(accessToken, profileData.getUserId())`
- maps request to `UpdateProfileCommand`
- invokes `UpdateUserProfileUseCase`
- returns updated `NebulaUserResponse`
- returns `403 FORBIDDEN` when token does not match the request owner

**Example payload**

```json
{
  "userId": 42,
  "firstName": "John",
  "lastName": "Doe",
  "birthdate": "1993-01-01",
  "nationalityId": 1,
  "genderId": 2
}
```

### 3. Update user settings

**Endpoint**

- `PUT /api/v1/users/settings`

**Authentication**

- requires `accessToken` cookie
- token must belong to the same `userId` as provided in the payload

**Request body**

- `UserSettingsRequest`

Structure:

- `userId`
- `general`
  - `userId`
  - `theme`
- `sound`
  - `userId`
  - `muted`
  - `battleCry`
  - `volumeMaster`
  - `volumeMusic`
  - `volumeEffects`
  - `volumeVoices`

**Behavior**

- validates ownership with `tokenProvider.isValid(accessToken, request.userId())`
- maps request to a domain `UserSettings` aggregate
- invokes `UpdateUserSettingsUseCase`
- returns `UserSettingsResponse`
- returns `403 FORBIDDEN` when token does not match the request owner

**Example payload**

```json
{
  "userId": 42,
  "general": {
    "userId": 42,
    "theme": {
      "id": 17,
      "name": "Default"
    }
  },
  "sound": {
    "userId": 42,
    "muted": false,
    "battleCry": true,
    "volumeMaster": 100,
    "volumeMusic": 80,
    "volumeEffects": 90,
    "volumeVoices": 70
  }
}
```

## Related User-Facing Endpoints

### User achievements

These endpoints are strongly related to the user area because they operate on the current authenticated user or on user-owned resources.

Base path:

- `/api/v1/users/achievements`

Available operations:

- `GET /api/v1/users/achievements/{userId}/{achievementId}`
- `GET /api/v1/users/achievements/list`
- `GET /api/v1/users/achievements`

Notes:

- all of them use `accessToken` cookie
- the single-achievement endpoint additionally verifies that path `userId` matches token `userId`
- paged endpoint supports:
  - `page`
  - `size`
  - `sortBy`
  - `sortOrder`
  - `level`
  - `filterType`

### Avatar upload

Base path:

- `/api/v1/image`

Available operation:

- `POST /api/v1/image`

Notes:

- requires `accessToken` cookie
- expects multipart field `file`
- validates empty files and supported image extensions
- returns:
  - `201 CREATED` on success
  - `400 BAD_REQUEST` for empty/unsupported file
  - `401 UNAUTHORIZED` for invalid token
  - `403 FORBIDDEN` when storage fails

### Token metadata for the current user

Base path:

- `/api/v1/token`

Relevant endpoints for the user area:

- `GET /api/v1/token`
- `GET /api/v1/token/valid`
- `GET /api/v1/token/roles`
- `GET /api/v1/token/email`
- `GET /api/v1/token/id`

These endpoints are useful for frontend session introspection and owner-aware UI logic.

## Validation and Ownership Rules

The `user` area relies on token-based ownership rather than URL-based Spring Security restrictions.

### Verified rules

- reading current user data is based on the authenticated cookie token
- profile update requires the payload `userId` to match token `userId`
- settings update requires the payload `userId` to match token `userId`
- settings consistency is additionally checked in the application service, where nested `userId` values must match the root `userId`

## Response Mapping

The user module uses explicit REST mapping instead of exposing persistence entities directly.

### Main response types

- `NebulaUserResponse`
- `UserSettingsResponse`

### Main mapper

- `UserRestMapper`

This keeps controller contracts stable even if persistence or domain internals evolve.

## Persistence and Domain Notes

The current implementation gained strong test coverage because the architecture separates:

- REST contracts
- application use cases
- persistence mapping
- domain builders

In practice, this makes the user area easier to reason about and safer to evolve. Mapper, adapter, builder and ID tests now verify many of the transformation rules that previously remained implicit.

## Typical Frontend Flow

A common user-facing flow looks like this:

1. login through `/api/v1/account/token`
2. receive `accessToken` and `refreshToken` cookies
3. call `/api/v1/users` to load profile
4. call `/api/v1/users/settings` or `/api/v1/users/profile` to persist changes
5. optionally load `/api/v1/users/achievements/list`
6. use `/api/v1/token/*` endpoints for token metadata or refresh flow support

## Related Documents

- `docs/ENDPOINTS.md`
- `docs/SECURITY.md`
- `docs/ARCHITECTURE.md`
- `docs/TESTING.md`

