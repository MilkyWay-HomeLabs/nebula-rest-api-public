# Metrics Documentation

The Nebula REST API exposes operational metrics through Spring Boot Actuator and Micrometer with Prometheus scraping enabled. In addition to standard JVM, HTTP, and datasource metrics, the application now publishes business-oriented counters, timers, and summaries through a dedicated hexagonal port.

## Monitoring Architecture

Custom metrics follow the same architectural style as the rest of the application:

- `shared/application/port/out/MetricsPort` defines the outbound contract used by services and adapters.
- `shared/adapter/out/metrics/MicrometerMetricsAdapter` is the Micrometer-backed implementation.
- Business services stay independent of Micrometer APIs and only depend on the port.

This keeps metrics instrumentation easy to test and prevents the application layer from depending directly on a concrete monitoring library.

## Public-Preview Alignment

The current metrics setup is suitable for source publication in a public orphan branch because:

- metric names are technology-neutral and business-oriented
- Micrometer is hidden behind `MetricsPort`, so the application layer is not tied directly to Prometheus APIs
- no private credentials or scraper secrets are embedded in the metrics code or documentation

At the same time, the public preview is **not deployment-ready by default**. The current codebase still expects each deployment to decide:

- who may scrape `/api/actuator/prometheus`
- whether scraper traffic is authenticated at the application, proxy, or network layer
- whether `/api/actuator/metrics` should remain reachable in the target environment

## Actuator Base Path and Exposed Endpoints

All actuator endpoints are exposed under:

- `/api/actuator`

The currently configured web-exposed endpoints are:

- **Prometheus Metrics**: `/api/actuator/prometheus`
- **Health Information**: `/api/actuator/health`
- **Application Info**: `/api/actuator/info`
- **General Metrics**: `/api/actuator/metrics`

Verified properties:

```properties
management.endpoints.web.base-path=/api/actuator
management.endpoints.web.exposure.include=prometheus,health,info,metrics
management.endpoint.prometheus.access=unrestricted
management.endpoint.health.show-details=always
management.metrics.tags.application=${spring.application.name}
```

## Built-In Metrics

The Prometheus endpoint includes the standard Micrometer / Spring Boot Actuator families, including:

- JVM metrics
- HTTP server request metrics
- datasource / connection pool metrics
- process and system metrics

All exported meters inherit the application tag configured through:

```properties
management.metrics.tags.application=${spring.application.name}
```

## Custom Health Timers

`HealthController` still exposes timed request metrics through `@Timed` annotations:

- `http.api.v1.health.hello`
- `http.api.v1.health.version`

Those timers publish percentiles `0.5`, `0.95`, and `0.99`.

## Business Metrics

### Account Operations

`AccountCommandService` records:

- `nebula.account.operations`
- `nebula.account.operation.duration`

Supported `operation` tag values:

- `register`
- `confirm`
- `unlock`
- `reset-password`
- `generate-token`
- `change-password`

`nebula.account.operations` uses the `stage` tag with these values:

- `attempt`
- `success`
- `failure`
- `error`

### Image Uploads

`ImageService` records:

- `nebula.image.operations`
- `nebula.image.operation.duration`
- `nebula.image.upload.bytes`

Supported tags:

- `operation=upload`
- `stage=attempt|success|failure|error` for `nebula.image.operations`

`nebula.image.upload.bytes` is a distribution summary that records uploaded file sizes.

### External Auth Service Calls

Outbound adapters that talk to the Andromeda authorization service record:

- `nebula.authservice.requests`
- `nebula.authservice.request.duration`

Supported `operation` tag values:

- `register`
- `confirm`
- `unlock`
- `reset-password`
- `generate-token`
- `change-password`
- `get-account`
- `refresh-access`

`nebula.authservice.requests` uses the `stage` tag with:

- `attempt`
- `success`
- `failure`
- `error`

## Example PromQL Queries

Account registration success rate:

```promql
sum(rate(nebula_account_operations_total{operation="register",stage="success"}[5m]))
/
sum(rate(nebula_account_operations_total{operation="register",stage="attempt"}[5m]))
```

95th percentile of account operation latency:

```promql
histogram_quantile(
  0.95,
  sum by (le, operation) (rate(nebula_account_operation_duration_seconds_bucket[5m]))
)
```

Average uploaded avatar size:

```promql
sum(rate(nebula_image_upload_bytes_sum[5m]))
/
sum(rate(nebula_image_upload_bytes_count[5m]))
```

Auth service error volume:

```promql
sum(rate(nebula_authservice_requests_total{stage="error"}[5m])) by (operation)
```

## Security Configuration

### Current verified state

Actuator access is partially restricted in `AppConfig.securityFilterChain(...)`:

- `/api/actuator/health/**` and `/api/actuator/info` are public
- `/api/actuator/prometheus` requires authentication
- all remaining `/api/actuator/**` endpoints require authentication

This means the Spring Security rules are more restrictive than the `management.endpoint.prometheus.access=unrestricted` property alone would suggest. In practice, the security filter chain is the effective gate for web access.

### Recommendation

For production, keep Prometheus scraping behind trusted infrastructure and authenticate scraper traffic. If you want stricter hardening, also review network policy, ingress rules, and whether `/api/actuator/metrics` should remain accessible to authenticated application users.

## Notes and Boundaries

- `/api/actuator/*` endpoints are operational endpoints managed by Spring Boot Actuator.
- `/api/v1/*` business endpoints emit custom business metrics through `MetricsPort` where appropriate.
- The metric names documented here are based on the current implementation in the codebase.

