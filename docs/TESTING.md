# Testing and Coverage Report

The Nebula REST API uses an architecture-aligned test strategy: tests follow the same boundaries as the codebase itself, i.e. inbound adapters, outbound adapters, application services, mappers, assemblers, builders, domain models and security utilities. This approach made it possible to raise the global coverage above 90% for all aggregate metrics while keeping tests focused and fast.

## Coverage Summary

The following statistics reflect the current report for the full application scope (`all classes`).

| Metric   | Coverage % | Absolute Value |
|:---------|:-----------|:---------------|
| Classes  | 97.8%      | 131 / 134      |
| Methods  | 92.9%      | 407 / 438      |
| Branches | 90.1%      | 210 / 233      |
| Lines    | 91.9%      | 1152 / 1253    |

### What changed

The largest gains came from extending tests around the architectural seams of the system:

- inbound REST adapters such as `account`, `gender`, `nationality`, `token` and `user`
- HATEOAS assemblers in `gender.adapter.in.rest.assembler` and `nationality.adapter.in.rest.assembler`
- outbound persistence adapters for `user` and `userachievement`
- persistence mappers in the `user` module
- domain builders in `user.domain.builder.impl`
- DTO response/value objects such as `JwtTokenResponse`
- support services such as `HealthService`

In practice, this means the test suite no longer focuses only on controllers and a few services; it now covers the conversion and orchestration layers that connect the hexagonal architecture together.

### Recent additions for metrics coverage

The latest test expansion also covers the custom metrics layer introduced for Prometheus and Micrometer integration.

Focused unit tests now verify:

- `AccountCommandServiceTest` — account operation counters and duration metrics for success, failure, and error paths
- `ImageServiceTest` — upload counters, duration metrics, and uploaded-bytes summaries
- `HttpAuthServiceAdapterTest` — outbound auth-service request metrics for success, failure, and error flows
- `TokenAuthRefreshAdapterTest` — refresh-access metrics for success, failure, and error flows
- `MicrometerMetricsAdapterTest` — low-level Micrometer counter, timer, summary, and tag validation behavior

This matters because the metrics layer is deliberately placed across application services and outbound adapters rather than hidden in controllers. The tests therefore validate instrumentation at the same architectural seams where the business logic lives.

## Architecture Changes and Their Impact on Tests

### 1. Clearer hexagonal boundaries

The codebase is organized around ports-and-adapters:

- `adapter.in.*` for REST entry points
- `application.service` and `application.port.*` for use cases
- `adapter.out.*` for persistence and external integrations
- `domain.*` for business models, builders and exceptions

This separation made it easier to test each layer independently. Instead of relying on broad end-to-end scenarios, tests now verify each architectural boundary in isolation.

### 2. Mapping and transformation became first-class test targets

The architecture contains many transformation points:

- REST request/response mappers
- persistence mappers
- HATEOAS assemblers
- DTO wrappers and response objects

Once these classes were treated as explicit architectural components, they received dedicated unit tests. This directly improved coverage for packages such as:

- `org.derleta.nebula.account.adapter.in.rest.dto.response`
- `org.derleta.nebula.gender.adapter.in.rest.assembler`
- `org.derleta.nebula.nationality.adapter.in.rest.assembler`
- `org.derleta.nebula.user.adapter.out.persistence.mapper`

### 3. Builders and value objects reduced hidden logic

The `user` area in particular uses builder classes and composite identifiers. By testing builders and ID/value semantics directly, the suite now validates:

- object construction rules
- `equals` / `hashCode` branches
- null-handling paths
- defaulting behavior inside mappers

This is why `org.derleta.nebula.user.domain.builder.impl` moved to full coverage and why `user.adapter.out.persistence.entity.id` and `userachievement.adapter.out.persistence.entity.id` now have complete branch coverage.

### 4. Outbound adapters are tested through ports and mocked repositories

Persistence adapters such as `UserJpaAdapter` and `UserAchievementJpaAdapter` are now verified without spinning up full infrastructure for every scenario. Mocking repository ports allows the tests to validate:

- object mapping from JPA entities to domain models
- delegation to repositories
- paging and query object handling
- behavior for empty, found and not-found flows

This architectural choice translated directly into major improvements in:

- `org.derleta.nebula.user.adapter.out.persistence`
- `org.derleta.nebula.user.adapter.out.persistence.mapper`
- `org.derleta.nebula.userachievement.adapter.out.persistence`

## Coverage Breakdown

Below is a condensed, architecture-oriented breakdown showing the most important areas of improvement and the main remaining weak spots.

### Fully covered or near-fully covered architectural areas

| Package | Class % | Method % | Branch % | Line % |
| :--- | :--- | :--- | :--- | :--- |
| `org.derleta.nebula.account.adapter.in.rest` | 100% | 100% | 100% | 100% |
| `org.derleta.nebula.account.adapter.in.rest.dto.response` | 100% | 100% | 100% | 100% |
| `org.derleta.nebula.gender.adapter.in.rest.assembler` | 100% | 100% | 100% | 100% |
| `org.derleta.nebula.nationality.adapter.in.rest.assembler` | 100% | 100% | 100% | 100% |
| `org.derleta.nebula.health.application.service` | 100% | 100% | 100% | 100% |
| `org.derleta.nebula.user.adapter.out.persistence` | 100% | 100% | 100% | 100% |
| `org.derleta.nebula.user.adapter.out.persistence.mapper` | 100% | 100% | 100% | 100% |
| `org.derleta.nebula.user.domain.builder.impl` | 100% | 100% | 100% | 100% |
| `org.derleta.nebula.userachievement.adapter.out.persistence` | 100% | 100% | 100% | 100% |
| `org.derleta.nebula.userachievement.adapter.out.persistence.entity.id` | 100% | 100% | 100% | 100% |
| `org.derleta.nebula.token.domain.types` | 100% | 100% | 100% | 100% |

### Important areas that remain below 90% in at least one metric

| Package | Class % | Method % | Branch % | Line % |
| :--- | :--- | :--- | :--- | :--- |
| `org.derleta.nebula.account.adapter.out.authservice` | 66.7% | 83.9% | 77.5% | 78.8% |
| `org.derleta.nebula.account.adapter.out.persistence` | 50% | 10% | 0% | 11.1% |
| `org.derleta.nebula.config` | 100% | 93.8% | 0% | 87.8% |
| `org.derleta.nebula.game.adapter.out.persistence` | 50% | 28.6% | 100% | 19.2% |
| `org.derleta.nebula.image.adapter.out.storage` | 100% | 93.3% | 88.5% | 89.8% |
| `org.derleta.nebula.shared.security` | 100% | 100% | 75% | 95.5% |
| `org.derleta.nebula.token.adapter.in.rest` | 100% | 100% | 83.3% | 93.5% |
| `org.derleta.nebula.user.adapter.out.persistence.entity` | 100% | 100% | 50% | 75% |

These packages are the natural next candidates for additional work, especially where behavior still depends on infrastructure-heavy code paths or framework configuration branches.

## Testing Strategy

### Technologies Used

- **JUnit 5**: core testing framework
- **Mockito**: isolation of ports, repositories and external collaborators
- **Spring Boot Test**: slice and integration-style verification where useful
- **MockMvc / controller tests**: verification of REST contracts without requiring a full deployed environment
- **JaCoCo / IntelliJ IDEA Coverage**: coverage analysis and HTML reporting
- **Testcontainers / database-backed tests**: selected repository and persistence verification

### Test Categories

- **Inbound adapter tests**: controllers, request/response mapping and HATEOAS assemblers
- **Application service tests**: use-case orchestration through `port.in` / `port.out`
- **Outbound adapter tests**: persistence adapters tested with mocked repositories or focused repository tests
- **Mapper tests**: REST and persistence object transformations validated independently
- **Builder and value-object tests**: domain builders, DTOs, records and composite IDs
- **Security tests**: JWT validation, token parsing and access control flows
- **Observability tests**: custom metrics emission, tag semantics, and Micrometer adapter behavior

### How architecture translated into testing

The current suite mirrors the architecture deliberately:

- if logic sits in a mapper, it has a mapper test
- if behavior sits in an adapter, it has an adapter test
- if object construction is delegated to a builder, it has a builder test
- if a domain/support class contains branching behavior (`equals`, null handling, defaults), it has targeted branch tests

The same rule now applies to observability:

- if a service emits business metrics, those counters and timers are verified in the service test
- if an outbound adapter emits integration metrics, those counters and timers are verified in the adapter test
- if a shared infrastructure adapter wraps Micrometer, it has a dedicated adapter-level unit test

This keeps tests small, deterministic and aligned with the responsibility of each package.

## Running Tests

To execute the full test suite and regenerate coverage reports:

```bash
./mvnw clean test
```

To run the main test suite while excluding integration tests that use the `*IT` naming convention:

```bash
./mvnw test -Dexclude="**/*IT.java"
```

If a dedicated test profile is needed locally:

```bash
./mvnw test -Ptest
```

Coverage reports are available in:

- `htmlReport/index.html`
- `target/site/jacoco/index.html`

## CI/CD Integration

Tests are executed automatically in CI. A successful pipeline requires the suite to pass and keeps the current architecture-oriented testing strategy stable across future changes.
