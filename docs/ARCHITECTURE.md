# Architecture — Nebula REST API

## Table of Contents

1. [Overview](#1-overview)
2. [Architectural Style](#2-architectural-style)
3. [Package Structure](#3-package-structure)
4. [Bounded Contexts](#4-bounded-contexts)
5. [Module Internals — Hexagonal Layout](#5-module-internals--hexagonal-layout)
6. [Cross-Module Dependencies](#6-cross-module-dependencies)
7. [Shared Module](#7-shared-module)
8. [Configuration Module](#8-configuration-module)
9. [Migration Summary](#9-migration-summary)
10. [Test Structure](#10-test-structure)
11. [Technology Stack](#11-technology-stack)

---

## 1. Overview

**Nebula REST API** is a Spring Boot 3 application serving as the backend for the Nebula gaming platform.
It communicates with an external **Andromeda Auth Server** for authentication/authorization and uses a
**MariaDB** relational database for persistence.

The codebase was refactored from a flat, layered package structure to a **Domain-Driven Design (DDD)**
approach with **Hexagonal Architecture (Ports & Adapters)**, organizing code into self-contained
**Bounded Contexts**.

---

## 2. Architectural Style

### Hexagonal Architecture (Ports & Adapters)

```
                          ┌─────────────────────────────────────────┐
                          │             Bounded Context              │
                          │                                          │
   ┌──────────────┐       │  ┌─────────────┐    ┌────────────────┐  │       ┌──────────────┐
   │  REST Client │──────►│  │  Inbound    │    │   Application  │  │       │   Database   │
   │  (Browser /  │       │  │  Adapter    │───►│   Service      │──┼──────►│   (MariaDB)  │
   │   Frontend)  │◄──────│  │  (REST      │    │                │  │       │              │
   └──────────────┘       │  │  Controller)│    │  Uses Ports:   │  │       └──────────────┘
                          │  └─────────────┘    │  - Input Ports │  │
                          │                     │  - Output Ports│  │       ┌──────────────┐
                          │  ┌─────────────┐    └────────────────┘  │       │  Andromeda   │
                          │  │  Outbound   │◄───────────────────────┼──────►│  Auth Server │
                          │  │  Adapter    │                        │       │  (HTTP)      │
                          │  │  (JPA /     │                        │       └──────────────┘
                          │  │   HTTP)     │                        │
                          │  └─────────────┘                        │
                          │                                          │
                          │  ┌─────────────────────────────────────┐│
                          │  │           Domain Model               ││
                          │  │  (Entities, Value Objects, Rules)    ││
                          │  └─────────────────────────────────────┘│
                          └─────────────────────────────────────────┘
```

### DDD Bounded Contexts

Each feature domain is isolated in its own bounded context:

```
org.derleta.nebula
├── account/          ← Account registration, login, password management
├── game/             ← Game catalogue management
├── gender/           ← Reference data: genders
├── health/           ← Application health check
├── image/            ← Avatar / image upload
├── nationality/      ← Reference data: nationalities and regions
├── theme/            ← UI theme catalogue
├── token/            ← JWT token validation & refresh
├── user/             ← User profile management
├── userachievement/  ← Achievements system
├── shared/           ← Cross-cutting concerns (security, exceptions, DTOs, metrics)
└── config/           ← Spring framework configuration
```

---

## 3. Package Structure

### Full Tree

```
org.derleta.nebula
│
├── NebulaRestApiApplication.java
│
├── config/
│   ├── AppConfig.java               ← Spring Security, CORS, RestTemplate
│   ├── security/
│   │   ├── JwtAuthenticationFilter.java
│   │   └── JwtUserPrincipal.java
│   ├── WebMvcConfig.java            ← HATEOAS, pagination resolvers
│   └── ServletInitializer.java      ← WAR bootstrap
│
├── shared/
│   ├── application/
│   │   └── port/out/
│   │       └── MetricsPort.java
│   ├── adapter/out/
│   │   └── metrics/
│   │       └── MicrometerMetricsAdapter.java
│   ├── domain/
│   │   ├── exception/
│   │   │   ├── TokenExpiredException.java
│   │   │   ├── HttpRequestException.java
│   │   │   └── MissingHeaderException.java
│   │   └── types/
│   │       └── AppCode.java
│   ├── security/
│   │   ├── JwtTokenUtil.java
│   │   ├── TokenProvider.java
│   │   ├── TokenProviderImpl.java
│   │   └── model/
│   │       ├── Role.java
│   │       ├── TokenData.java
│   │       ├── TokenDataBuilder.java
│   │       └── TokenDataBuilderImpl.java
│   └── adapter/in/rest/
│       ├── dto/
│       │   ├── Response.java
│       │   ├── ErrorResponse.java
│       │   └── ResponseWithCookieHeaders.java
│       └── exception/
│           └── GlobalExceptionHandler.java
│
├── account/
│   ├── domain/types/
│   │   ├── AccountProcessType.java
│   │   └── AccountResponseType.java
│   ├── application/
│   │   ├── port/in/
│   │   │   ├── ChangePasswordUseCase.java
│   │   │   ├── ConfirmAccountUseCase.java
│   │   │   ├── GenerateTokenUseCase.java
│   │   │   ├── RegisterAccountUseCase.java
│   │   │   ├── ResetPasswordUseCase.java
│   │   │   └── UnlockAccountUseCase.java
│   │   ├── port/out/
│   │   │   ├── AccountRepositoryPort.java
│   │   │   └── AuthServicePort.java
│   │   └── service/
│   │       └── AccountCommandService.java
│   └── adapter/
│       ├── in/rest/
│       │   ├── AccountController.java
│       │   └── dto/request|response/
│       ├── out/authservice/
│       │   ├── HttpAuthServiceAdapter.java
│       │   ├── HttpAuthClient.java
│       │   ├── ValidationUtil.java
│       │   ├── ResponseHandler.java
│       │   └── dto/  (AccountResponse, UserRoles, AuthTokenRequest, ...)
│       └── out/persistence/
│           ├── AccountJpaAdapter.java
│           └── IdUtil.java
│
├── gender/
│   ├── domain/
│   │   ├── model/Gender.java
│   │   └── exception/GenderNotFoundException.java
│   ├── application/
│   │   ├── port/in/  (GetGenderUseCase, GetAllGendersUseCase)
│   │   ├── port/out/ (GenderRepositoryPort)
│   │   └── service/GenderService.java
│   └── adapter/
│       ├── in/rest/  (GenderController, dto/, mapper/, assembler/)
│       └── out/persistence/ (GenderJpaAdapter, entity/, jpa/, mapper/)
│
├── nationality/      ← Same structure as gender, includes Region sub-domain
├── health/           ← Simple health check controller
├── theme/            ← Theme CRUD with same hexagonal layout
├── game/             ← Game catalogue with paged queries, specifications
├── userachievement/  ← Achievements with composite keys
├── user/             ← Full user profile with nested settings
├── image/            ← File upload adapter
└── token/            ← JWT operations + refresh via Andromeda Auth
```

---

## 4. Bounded Contexts

### 4.1 `account`

Manages account lifecycle via the external **Andromeda Auth Server**.

**Use Cases (Input Ports):**

| Interface | Method | Description |
|---|---|---|
| `RegisterAccountUseCase` | `register(Request)` | Creates a new account |
| `ConfirmAccountUseCase` | `confirm(UserConfirmationRequest)` | Confirms email |
| `UnlockAccountUseCase` | `unlock(Long id)` | Unlocks a locked account |
| `ResetPasswordUseCase` | `resetPassword(String email)` | Initiates password reset |
| `GenerateTokenUseCase` | `generateToken(AuthEmailRequest)` | Issues JWT tokens |
| `ChangePasswordUseCase` | `changePassword(String jwt, PasswordUpdateRequest)` | Updates password |

**Output Ports:**

| Interface | Implementations |
|---|---|
| `AccountRepositoryPort` | `AccountJpaAdapter` (MariaDB via JPA) |
| `AuthServicePort` | `HttpAuthServiceAdapter` → `HttpAuthClient` (HTTP to Andromeda) |

---

### 4.2 `gender` / `nationality`

Reference data bounded contexts. Read-only from REST perspective.

**Use Cases:** `GetGenderUseCase`, `GetAllGendersUseCase`
**Nationality** also contains the `Region` sub-domain.

---

### 4.3 `game`

Full CRUD with paged queries, filtering via JPA Specifications.

**Use Cases:** `GetGameUseCase`, `GetAllGamesUseCase` (paged), `CreateGameUseCase`, `UpdateGameUseCase`, `DeleteGameUseCase`
**Output Port:** `GameRepositoryPort` → `GameJpaAdapter`

---

### 4.4 `theme`

UI theme reference data. Similar to `game`.

---

### 4.5 `userachievement`

Manages achievements assigned to users. Uses composite JPA keys (`UserAchievementId`, `AchievementLevelId`).

```
Achievement
  └── AchievementLevel (many)

UserAchievement
  ├── references: User (via UserAchievementId)
  └── references: Achievement
```

---

### 4.6 `user`

Full user profile. Most complex bounded context — embeds references to other domains.

**Domain model:**
```
NebulaUser
  ├── Gender         (from gender BC)
  ├── Nationality    (from nationality BC)
  ├── UserSettings
  │     ├── UserSettingsGeneral → Theme (from theme BC)
  │     └── UserSettingsSound
  ├── List<Game>     (from game BC)
  └── List<UserAchievement>  (from userachievement BC)
```

**Use Cases:** `GetUserUseCase`, `UpdateProfileUseCase`, `UpdateUserSettingsUseCase`

---

### 4.7 `image`

Handles avatar uploads. Delegates to `ImageStorageAdapter` and `ImageUtil`.

---

### 4.8 `token`

Validates and refreshes JWT tokens. Delegates refresh to `TokenAuthRefreshAdapter` → `HttpAuthClient`.

---

## 5. Module Internals — Hexagonal Layout

Each bounded context follows this internal structure:

```
{module}/
├── domain/
│   ├── model/          ← Pure Java records/classes — no framework deps
│   ├── exception/      ← Domain-specific exceptions
│   ├── builder/        ← Builder interfaces + implementations
│   └── types/          ← Enums specific to this domain
│
├── application/
│   ├── port/
│   │   ├── in/         ← Use Case interfaces (Input Ports)
│   │   └── out/        ← Repository/Service interfaces (Output Ports)
│   └── service/        ← Application services implementing use cases
│
└── adapter/
    ├── in/
    │   └── rest/       ← Spring @RestController + DTOs + mappers
    └── out/
        ├── persistence/ ← JPA entities, repositories, mappers, adapters
        └── authservice/ ← HTTP client adapters (account module only)
```

### Dependency Rule

```
adapter/in/rest  →  application/service  →  domain/model
                         ↑                       ↑
                    port/in (uses)          port/out (implements)
                         ↑
                   adapter/out/persistence
```

> **The domain model has zero dependencies on Spring or JPA.**
> Adapters depend on the domain — never the other way around.

---

## 6. Cross-Module Dependencies

In this monolithic application, some JPA entities reference entities from other modules.
This is intentional and acceptable within a monolith:

```
UserEntity
  ├── GenderJpaEntity          (gender module)
  ├── NationalityJpaEntity     (nationality module)
  ├── GameEntity               (game module)
  └── UserAchievementEntity    (userachievement module)

UserSettingsGeneralEntity
  └── ThemeEntity              (theme module)

UserAchievementEntity
  └── UserEntity               (user module)
```

Domain models also reference across modules at the service/mapper level:

```
NebulaUser (user domain)
  ├── Gender         (gender.domain.model)
  ├── Nationality    (nationality.domain.model)
  ├── Theme          (theme.domain.model)   via UserSettingsGeneral
  ├── Game           (game.domain.model)
  └── UserAchievement (userachievement.domain.model)
```

---

## 7. Shared Module

Houses cross-cutting concerns that do not belong to any single bounded context:

```
shared/
├── domain/
│   ├── exception/
│   │   ├── TokenExpiredException    ← thrown when JWT has expired
│   │   ├── HttpRequestException     ← wraps HTTP errors from Andromeda
│   │   └── MissingHeaderException   ← missing required HTTP header
│   └── types/
│       └── AppCode                  ← application-wide response codes
│
├── security/
│   ├── JwtTokenUtil                 ← low-level JWT parsing (io.jsonwebtoken)
│   ├── TokenProvider (interface)    ← port: isValid, getUserId, getEmail, getRoles
│   ├── TokenProviderImpl            ← implements TokenProvider via JwtTokenUtil
│   └── model/
│       ├── Role                     ← user role (id + name)
│       ├── TokenData                ← parsed token payload
│       ├── TokenDataBuilder         ← builder interface
│       └── TokenDataBuilderImpl     ← builder implementation
│
└── adapter/in/rest/
    ├── dto/
    │   ├── Response                 ← base response wrapper
    │   ├── ErrorResponse            ← error payload
    │   └── ResponseWithCookieHeaders← response carrying Set-Cookie headers
    └── exception/
        └── GlobalExceptionHandler   ← @ControllerAdvice for all modules
```

Cross-cutting runtime security integration that depends directly on Spring Security lives in `config/security/`. This keeps framework-specific request authentication wiring out of the domain-oriented `shared/security/` package, which remains focused on JWT parsing and token abstraction.

---

## 8. Configuration Module

Spring framework wiring — not a bounded context:

```
config/
├── AppConfig.java
│     ├── @EnableWebSecurity
│     ├── SecurityFilterChain  (public/private route split, JWT auth filter, CSRF cookie support)
│     ├── CorsConfigurationSource (origin allowlist loaded from properties)
│     └── RestTemplate @Bean
│
├── security/
│     ├── JwtAuthenticationFilter  (extracts token from cookie/header and populates SecurityContext)
│     └── JwtUserPrincipal         (authenticated principal used by the filter)
│
├── WebMvcConfig.java
│     ├── @EnableHypermediaSupport(HAL)
│     ├── HateoasSortHandlerMethodArgumentResolver
│     ├── HateoasPageableHandlerMethodArgumentResolver
│     ├── PagedResourcesAssembler
│     └── PagedResourcesAssemblerArgumentResolver
│
└── ServletInitializer.java
      └── SpringBootServletInitializer (WAR deployment)
```

---

## 9. Migration Summary

### Before: Flat Layered Structure

```
org.derleta.nebula
├── controller/          ← All REST controllers in one package
├── service/             ← All services in one package
├── repository/          ← All JPA repositories in one package
├── domain/
│   ├── model/           ← All domain classes mixed together
│   ├── entity/          ← All JPA entities
│   ├── mapper/          ← All mappers
│   ├── builder/         ← All builders
│   ├── types/           ← All enums
│   ├── rest/            ← Auth server DTOs
│   └── token/           ← Token model
├── exceptions/          ← All exceptions
├── util/                ← Utility classes
└── config/              ← Spring config
```

### After: DDD Hexagonal Structure

```
org.derleta.nebula
├── account/   game/   gender/   health/
├── image/     nationality/   theme/
├── token/     user/   userachievement/
├── shared/
└── config/
```

### Migration Steps Performed

| Step | What moved                                                                                 | Destination                                       |
|------|--------------------------------------------------------------------------------------------|---------------------------------------------------|
| 1    | `domain/types/AccountProcessType`, `AccountResponseType`                                   | `account/domain/types/`                           |
| 2    | `domain/types/TokenResponseType`, `AccessProcessType`, `AccessResponseType`                | `token/domain/types/`                             |
| 3    | `domain/types/AppCode`                                                                     | `shared/domain/types/`                            |
| 4    | `domain/rest/Role`                                                                         | `shared/security/model/`                          |
| 5    | `domain/rest/UserRoles`, `UserAccount`                                                     | `account/adapter/out/authservice/dto/`            |
| 6    | `domain/token/TokenData`                                                                   | `shared/security/model/`                          |
| 7    | `domain/builder/TokenDataBuilder[Impl]`                                                    | `shared/security/model/`                          |
| 8    | `exceptions/TokenExpiredException`                                                         | `shared/domain/exception/`                        |
| 9    | `exceptions/HttpRequestException`                                                          | `shared/domain/exception/`                        |
| 10   | `exceptions/MissingHeaderException`                                                        | `shared/domain/exception/`                        |
| 11   | `domain/model/Gender`                                                                      | `gender/domain/model/`                            |
| 12   | `domain/model/Nationality`, `Region`                                                       | `nationality/domain/model/`                       |
| 13   | `domain/model/Theme` + builder                                                             | `theme/domain/model/`, `theme/domain/builder/`    |
| 14   | `domain/model/Game` + builder                                                              | `game/domain/model/`, `game/domain/builder/`      |
| 15   | `domain/model/Achievement`, `AchievementLevel`, `UserAchievement`, `NebulaUserAchievement` | `userachievement/domain/model/`                   |
| 16   | `domain/model/NebulaUser`, `UserSettings*`                                                 | `user/domain/model/`                              |
| 17   | `domain/entity/GameEntity`                                                                 | `game/adapter/out/persistence/entity/`            |
| 18   | `domain/entity/ThemeEntity`                                                                | `theme/adapter/out/persistence/entity/`           |
| 19   | `domain/entity/UserEntity`, `UserSettings*Entity`                                          | `user/adapter/out/persistence/entity/`            |
| 20   | `domain/entity/AchievementEntity`, `UserAchievementEntity`, etc.                           | `userachievement/adapter/out/persistence/entity/` |
| 21   | `domain/mapper/GameMapper`                                                                 | `game/adapter/out/persistence/mapper/`            |
| 22   | `domain/mapper/ThemeMapper`                                                                | `theme/adapter/out/persistence/mapper/`           |
| 23   | `domain/mapper/NebulaUserMapper`, `UserSettings*Mapper`                                    | `user/adapter/out/persistence/mapper/`            |
| 24   | `domain/mapper/AchievementMapper`, `UserAchievementMapper`, etc.                           | `userachievement/adapter/out/persistence/mapper/` |
| 25   | `domain/mapper/NebulaUserAchievementMapper`                                                | `userachievement/domain/mapper/`                  |
| 26   | `util/HttpAuthClient`, `ValidationUtil`, `ResponseHandler`                                 | `account/adapter/out/authservice/`                |
| 27   | `util/IdUtil`                                                                              | `account/adapter/out/persistence/`                |
| 28   | `util/ImageUtil`                                                                           | `image/adapter/out/storage/`                      |
| 29   | `config/JwtTokenUtil`                                                                      | `shared/security/`                                |
| 30   | All controllers → bounded context `adapter/in/rest/`                                       | Per module                                        |
| 31   | All services → bounded context `application/service/`                                      | Per module                                        |
| 32   | All repositories → bounded context `adapter/out/persistence/jpa/`                          | Per module                                        |

### Classes Deleted (Dead Code)

| Class                                                      | Reason                               |
|------------------------------------------------------------|--------------------------------------|
| `domain/entity/UserGameEntity.java`                        | Zero usages found — never referenced |
| Old flat `controller/`, `service/`, `repository/` packages | Replaced by hexagonal adapters       |

---

## 10. Test Structure

Tests mirror the main source package structure:

```
src/test/java/org/derleta/nebula/
├── config/          ← AppConfigTest, AppSecurityIntegrationTest, JwtTokenUtilTest, WebMvcConfigTest
├── config/security/ ← JwtAuthenticationFilterTest
├── shared/          ← GlobalExceptionHandlerTest, TokenProviderImplTest
├── shared/adapter/out/metrics/ ← MicrometerMetricsAdapterTest
├── account/         ← AccountCommandServiceTest, AccountControllerTest, HttpAuthServiceAdapterTest
├── game/            ← GameControllerTest, GameServiceTest, GameJpaRepositoryTest
├── gender/          ← GenderControllerTest, GenderServiceTest
├── health/          ← HealthControllerTest
├── image/           ← ImageControllerTest, ImageServiceTest, ImageStorageAdapterTest
├── nationality/     ← NationalityControllerTest, NationalityServiceTest
├── theme/           ← ThemeControllerTest, ThemeServiceTest
├── token/           ← TokenAuthRefreshAdapterTest, TokenRestMapperTest
├── user/            ← UserControllerTest, UserRestMapperTest, UserServiceTest
├── userachievement/ ← UserAchievementControllerTest, UserAchievementServiceTest
├── domain/mapper/   ← Unit tests for all persistence mappers (legacy location, kept for compatibility)
├── util/            ← HttpAuthClientTest, ImageUtilTest, IdUtilTest, ValidationUtilTest
└── testcontainer/   ← Testcontainers base config (MariaDB)
```

The suite includes unit, slice, integration, and security-focused tests aligned with the package boundaries above.

Test types used:
- **Unit tests** (`@ExtendWith(MockitoExtension.class)`) — services, mappers, controllers
- **Slice tests** (`@DataJpaTest`) — JPA repositories with Testcontainers MariaDB
- **Integration tests** (`@SpringBootTest`) — HTTP client tests with MockWebServer

---

## 11. Technology Stack

| Layer           | Technology                                   |
|-----------------|----------------------------------------------|
| Runtime         | Java 21 (Temurin)                            |
| Framework       | Spring Boot 3.x                              |
| Persistence     | Spring Data JPA + Hibernate + MariaDB        |
| Security        | Spring Security (JWT-authenticated routes, CSRF-protected browser flows) |
| JWT             | io.jsonwebtoken (JJWT)                       |
| REST            | Spring MVC + Spring HATEOAS (HAL)            |
| HTTP Client     | Apache HttpClient 5                          |
| Build           | Maven                                        |
| Testing         | JUnit 5 + Mockito + Testcontainers (MariaDB) |
| Docs            | OpenAPI / Springdoc                          |
| Code Generation | Lombok                                       |

---

## 12. Key Design Decisions

### Use Case per Interface
Each use case is a separate interface with a single method. This follows the **Interface Segregation Principle** and makes each capability independently mockable and replaceable.

### Static Mappers
Persistence mappers (e.g. `GameMapper`, `ThemeMapper`) are implemented as static utility classes rather than Spring beans. This makes them easily testable with Mockito's `mockStatic()` and removes the need for dependency injection in mapping logic.

### Cross-Module JPA References Accepted
In a monolith, JPA entities may reference entities from other bounded contexts. This is a pragmatic decision to avoid the overhead of event-driven decoupling where it is not yet needed.

### Shared Module as Anti-Corruption Layer
The `shared/` module acts as an **Anti-Corruption Layer** for security concerns — it owns the `TokenProvider` port which abstracts all JWT operations, so no bounded context directly depends on JJWT.

The same module also owns the cross-cutting metrics abstraction. `MetricsPort` lets application services emit business metrics without importing Micrometer types, while `MicrometerMetricsAdapter` provides the concrete infrastructure binding.

### Auth Server Isolation
All communication with the external **Andromeda Auth Server** is isolated in `account/adapter/out/authservice/`. The `AuthServicePort` interface in the application layer ensures the domain never knows about HTTP.

### Metrics Placement
Business metrics are emitted from application services such as `AccountCommandService` and `ImageService`, while outbound integration metrics are emitted from HTTP-based adapters such as `HttpAuthServiceAdapter` and `TokenAuthRefreshAdapter`. This keeps operational telemetry close to the workflow that owns it without pushing Micrometer-specific concerns into the domain model.

