# Nebula Rest Api Changelog

## v 5.4.0-PUBLIC-PREVIEW (2026-05-13)

### Metrics and observability
- Add a dedicated `MetricsPort` abstraction and Micrometer-backed adapter for custom business metrics.
- Instrument account command flows with counters and duration metrics for attempt, success, failure, and error stages.
- Instrument image upload and outbound Andromeda auth-service traffic with Prometheus-friendly custom metrics.
- Keep health and version endpoint timing metrics available alongside Actuator and Prometheus exposure.

### Testing
- Add focused unit tests for `MicrometerMetricsAdapter`.
- Add metrics coverage for `AccountCommandService`, `ImageService`, `HttpAuthServiceAdapter`, and `TokenAuthRefreshAdapter`.
- Verify success, failure, and error metric emission paths for the new instrumentation layer.

### Documentation
- Update `README.md` to describe the current public-preview security posture and metrics architecture.
- Update metrics and architecture documentation to reflect the shared metrics port, Micrometer adapter, and current security wiring.

## v 5.2.0-PUBLIC-PREVIEW (2026-05-07)

### Security
- Update Spring Boot Parent to version 3.5.14.
- Update Apache HttpClient 5 to version 5.6.1.
- Improve diagnostics for upstream auth-service errors by returning raw non-JSON error bodies when parsing fails.
- Continue alignment of Nebula ↔ Andromeda protected account flows in HTTPS/public-preview environments.

### Integration
- Update preview integration with Andromeda for `/andromeda/api/v1/...` context-path based communication.
- Improve change-password request handling and troubleshooting for external auth-service delegation.
- Verify outbound account requests against the HTTPS reverse-proxy path used in the preview environment.

### Testing
- Update `HttpAuthClientTest` assertions for the current password-change request behaviour.
- Refine MockWebServer-based coverage for account/auth-service request handling.

## v 5.1.1-SNAPSHOT (2025-04-16)

### Security
- Update Spring Boot Parent to version 3.5.13 to address security vulnerabilities and maintain compatibility with dependencies.
- Update SpringDoc OpenAPI Starter WebMVC UI to 2.8.17 version
- Introduce `CookieSanitizer` utility class to sanitize cookie values and prevent CRLF injection
  and HTTP Response Splitting attacks in token response headers.
- Replace raw `Set-Cookie` header construction in `TokenController` with sanitized values
  via `CookieSanitizer.sanitize()`.
- Resolve high-severity issues reported by Qodana static analysis (taint-flow / XSS sink
  in `TokenController.getResponseForRefreshAccess`); confirmed as false positive — cookies
  originate from the external auth-service `Set-Cookie` response headers, not from user input.
- `AccessResponse.cookiesHeaders` annotated `@JsonIgnore` — map is never serialised to the
  response body.

## v 5.0.0-PUBLIC (2025-04-16)

### Public release summary
- Publish the project as `5.0.0-PUBLIC`.
- Finalize the public documentation set for architecture, endpoints, security, user area, testing, and metrics.
- Raise automated test coverage above 90% for all aggregate metrics.

### Architecture and refactoring
- Rebuild the application around DDD / hexagonal architecture boundaries.
- Refactor shared domain utility classes to align with the ports-and-adapters design.
- Move account, health, theme, game, gender, nationality, user, token, and user-achievement flow into explicit REST adapters, application services, ports, and domain models.
- Refactor repository integration and persistence adapters for user and user-achievement modules.
- Consolidate token and response handling around shared DTO/security components.

### Functional changes
- Implement user management features in the new architecture, including profile and settings flows.
- Implement user-achievement management features with owner-aware access rules and paged queries.
- Implement game management features with public reads and admin-oriented write flows.
- Implement image upload and token-management flows in the new architecture.
- Add or complete account registration, confirmation, unlock, reset-password, login, and change-password REST flows.
- Add gender and nationality domain models together with persistence and REST mappings.
- Keep health and version endpoints available in the refactored architecture.

### API and security
- Introduce clearer separation between cookie-based owner endpoints and header-based admin endpoints.
- Expand token handling, token metadata endpoints, and access-token refresh flow.
- Improve response handling for token and account operations.
- Retain current permissive Spring Security route policy while documenting controller-level ownership and role checks.

### Testing and quality
- Add broad unit-test coverage for controllers, mappers, assemblers, builders, persistence adapters, DTOs, and support classes.
- Add unit tests for user-related entities, services, and persistence mapping.
- Extend test coverage for HATEOAS assemblers, token/domain types, and persistence identifiers.
- Keep OpenAPI smoke tests and configuration tests in the verification suite.

### Documentation and observability
- Update architecture documentation to reflect the DDD / hexagonal structure.
- Add `docs/ENDPOINTS.md` with a verified API catalog.
- Add `docs/SECURITY.md` with the current JWT, cookie and authorization model.
- Add `docs/USER.md` with user-area flows, payloads, and ownership rules.
- Update `docs/TESTING.md` with current coverage metrics and architecture-aligned test strategy.
- Update `docs/METRICS.md` to match the verified Actuator / Prometheus configuration.
- Update `README.md` for the public release and current project documentation set.

## v 4.2.6-SNAPSHOT
- Update CORS allowed origins to include new subdomains and remove deprecated ones.
- Add CORS configuration tests to validate allowed origins and methods.
- Update database connection properties for improved reliability.
- Improve directory validation in `ImageUpdater`.
- Add OpenAPI smoke tests for API documentation verification.

## v 4.1.0 PUBLIC
- Add image upload functionality with comprehensive integration tests.
- Enhance `AccountController` with better registration validation and response handling.
- Refactor application properties to streamline authentication URL configurations.
- Update server configuration for production environments.

## v 4.0.0 Release
- Major refactoring: Changed package structure from `pl.derleta.nebula` to `org.derleta.nebula`.
- Updated Spring Boot to version 3.5.11 (from 3.2.12).
- Moved documentation from `info/` to `docs/` directory.
- Updated `HELP.md` with Traefik SSL configuration and archived old self-signed certificate steps.
- Removed `JENKINS.md`, replaced with GitHub Actions CI/CD pipelines.
- Added CI/CD pipelines for building and testing (test environment) and building (prod environment).
- Added `TESTING.md` with detailed coverage reports and testing strategy.
- Added support for Prometheus metrics (`/api/actuator/prometheus`). See `METRICS.md` for more details.
- Removed `spring-cloud` dependency; settings are now loaded via Docker environment variables.
- Created `HealthController` with hello and version endpoints.
- Reverted default application port to 8080 and added technology stack information (Hibernate, Lombok, Swagger) to documentation.
- Updated `application.properties` and `README.md` accordingly.
- Expanded `GlobalExceptionHandler` to handle more exceptions (IllegalArgumentException, NotFound exceptions, GameAlreadyExistsException) and added comprehensive unit and integration tests.

## v 3.1.3 Release
- added CorsFilter to allow requests from the test and productional platform
- migrate cors allowed pages from classes to properties
- remove @CrossOrigin annotations from controllers

## v 3.1.1 Release
- update properties to work with test (docker) and prod (raspberry pi) environments
```properties
milkyway.tomcat.url=${MILKYWAY_TOMCAT_URL}
spring.cloud.config.uri=${milkyway.tomcat.url}
spring.config.import=optional:configserver:${milkyway.tomcat.url}/andromeda-cloud-server/cloud-authorization
```

## v 3.1.0 Release
- Added handling for expired token exceptions. Now endpoints return 401 Unauthorized status with body when any controller method using tokenProvider.isValid encounters an expired token:
  ```json
  {
    "message": "ACCESS_TOKEN_EXPIRED",
    "error": "TOKEN_EXPIRED",
    "timestamp": "2025-05-20T18:14:57.202066872"
  }
  ```

## v 3.0.0 Public Release
- Added over 400 unit and integration tests.
- Added an endpoint to refresh the accessToken (using the cookie with a valid refreshToken). Removed the old JWT token cookie system and replaced it with both accessToken and refreshToken, which are now stored as HttpOnly cookies. The accessToken is valid for 1 hour, while the refreshToken is valid for over 20 days.
- Fixed small errors and refactored some parts of the code.

## v 2.0.0 Beta Release
This version is intended for testing purposes in a pre-production environment.

## v1.0.0
- Alpha Release  
