# Nebula REST API

<p style="text-align: center;">
  <a href="https://github.com/MilkyWay-HomeLabs/nebula-rest-api-public">
    <img alt="Repo" src="https://img.shields.io/badge/Repo-GitHub-0f172a?style=for-the-badge&logo=github&logoColor=white">
  </a>
  <img alt="Version" src="https://img.shields.io/badge/Version-5.4.0--PUBLIC--PREVIEW-2563eb?style=for-the-badge">
  <img alt="Coverage" src="https://img.shields.io/badge/Coverage-91.9%25-16a34a?style=for-the-badge">
  <img alt="Release" src="https://img.shields.io/badge/Release-Public%20Preview-f59e0b?style=for-the-badge">
</p>

<p style="text-align: center;">
  <img alt="Java" src="https://img.shields.io/badge/Java-21-0b3d91?style=flat-square&logo=openjdk&logoColor=white">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.5.14-16a34a?style=flat-square&logo=springboot&logoColor=white">
  <img alt="Hibernate" src="https://img.shields.io/badge/Hibernate-ORM-59666C?style=flat-square&logo=hibernate&logoColor=white">
  <img alt="Maven" src="https://img.shields.io/badge/Maven-Build-c71a36?style=flat-square&logo=apachemaven&logoColor=white">
  <img alt="MariaDB" src="https://img.shields.io/badge/MariaDB-Database-003545?style=flat-square&logo=mariadb&logoColor=white">
  <img alt="Lombok" src="https://img.shields.io/badge/Lombok-Library-bc0203?style=flat-square">
  <img alt="Swagger" src="https://img.shields.io/badge/Swagger-OpenAPI-85ea2d?style=flat-square&logo=swagger&logoColor=black">
</p>

---

## 📖 Documentation

- 🏗️ [Architecture](docs/ARCHITECTURE.md) — hexagonal structure, module boundaries, and design decisions.
- 🌐 [Endpoints](docs/ENDPOINTS.md) — business REST API catalog and endpoint behavior.
- 🔐 [Security](docs/SECURITY.md) — JWT, cookies, authorization model, and security constraints.
- 👤 [User Area](docs/USER.md) — user-related endpoints, flows, payloads, and ownership rules.
- 📄 [Changelog](docs/CHANGELOG.md) — project history.
- 📊 [Metrics](docs/METRICS.md) — metrics and monitoring information.
- 🧪 [Testing](docs/TESTING.md) — testing instructions and coverage report.

---

## 🌟 Overview

**Nebula REST API** is a public-preview Spring Boot application for the **Andromeda platform**, exposing a REST API for account,
token, user, user-achievement, game, image, health, and reference-data operations. The project is structured around a
hexagonal architecture with inbound REST adapters, application use cases, outbound persistence/external adapters, and
explicit mapping layers.

> [!IMPORTANT]
> `5.4.0-PUBLIC-PREVIEW` is a public preview release. The current security posture is **still not production-hardened**:
> route-level authentication, a JWT authentication filter, and CSRF protection are enabled for the main API, but
> deployment-specific hardening such as trusted proxy setup, cookie policy review, scraper access control, and full
> external exposure validation still needs to be completed before using this service on the public Internet.

### Key Features

- 🔐 **Token-Based Access**: Cookie-based and header-based flows for login, token refresh, owner checks, and admin checks.
- 🗄️ **Data Management**: Efficient management of user settings, profile preferences, and achievements using **Hibernate ORM** and MariaDB.
- 🖼️ **Profile Customization**: Allows users to upload profile images and customize their personal settings and themes.
- 📈 **Monitoring & Metrics**: Integrated with Prometheus and Spring Boot Actuator for comprehensive performance tracking.
- 🔄 **CI/CD Integration**: Automated build, test, and deployment pipelines using GitHub Actions.
- 🧱 **DDD / Hexagonal Design**: Clear separation between `adapter.in`, `application`, `domain`, and `adapter.out` packages.
- 🛠️ **Modern Stack**: Utilizes **Lombok**, **Springdoc OpenAPI**, **Micrometer / Prometheus**, and focused mapper / assembler layers for API contracts.

### Monitoring and Diagnostics

The application provides both business diagnostics and operational endpoints.

Business diagnostics:

- `GET /api/v1/hello` — returns a simple "hello" message.
- `GET /api/v1/version` — returns the current application version.

Operational endpoints:

- `GET /api/actuator/health` — public
- `GET /api/actuator/info` — public
- `GET /api/actuator/metrics` — authenticated
- `GET /api/actuator/prometheus` — authenticated

Verified custom business metrics now include:

- `nebula.account.operations` and `nebula.account.operation.duration`
- `nebula.image.operations`, `nebula.image.operation.duration`, and `nebula.image.upload.bytes`
- `nebula.authservice.requests` and `nebula.authservice.request.duration`

The external auth-service metric family covers registration, account confirmation, unlock, password reset, token generation, password change, account lookup, and refresh-access traffic.

These metrics are emitted through a dedicated shared metrics port and Micrometer adapter, so the application layer stays decoupled from the concrete monitoring implementation. See [`docs/METRICS.md`](docs/METRICS.md) for metric names, tags, and example PromQL queries.

Focused unit tests verify the custom metrics instrumentation in:

- `AccountCommandService`
- `ImageService`
- `HttpAuthServiceAdapter`
- `TokenAuthRefreshAdapter`
- `MicrometerMetricsAdapter`

> [!WARNING]
> All endpoints should be verified carefully during the final stages of development to ensure security and proper
> functionality. Review all endpoints thoroughly, especially those handling sensitive data or authentication, to avoid potential
> vulnerabilities.

---

## 🛠️ API Examples

This application communicates with the Andromeda Authorization Server for parts of the account and token lifecycle.

### Health Check (Example)

```http
GET /api/v1/hello HTTP/1.1
Host: localhost:8080
```

### Version Check (Example)

```http
GET /api/v1/version HTTP/1.1
Host: localhost:8080
```

### Current User (Example)

```http
GET /api/v1/users HTTP/1.1
Host: localhost:8080
Cookie: accessToken=<jwt>
```

### Refresh Access Token (Example)

```http
POST /api/v1/token/refresh/access HTTP/1.1
Host: localhost:8080
Cookie: refreshToken=<jwt>
```

More examples in `.http` format for IntelliJ can be found in the `src/test/endpoints` directory.

---

## 🏗️ Building the Project

### Prerequisites

- **Java SDK 21**
- **Maven 3.x** (or use the included `./mvnw` wrapper)
- **MariaDB** (running instance)

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/MilkyWay-HomeLabs/nebula-rest-api-public.git
   cd nebula-rest-api-public
   ```

2. **Configuration (Environment Variables)**
   Configure the necessary environment variables in your system:

   ```bash
   HEADER_FOR_AUTH_SERVER=<your-header>
   APP_JWT_SECRET=<your-secret-key>
    APP_RESOURCES_BASE_URL=https://cdn.example.com/resources/nebula/
    CORS_ALLOWED_ORIGIN_PATTERNS=https://app.example.com,https://*.app.example.com
    IMAGE_AVATAR_PATH=/srv/nebula/avatars
    NEBULA_DB_URL=jdbc:mariadb://db.example.internal:3306/nebula
    NEBULA_USERNAME=<db_user>
    NEBULA_PASSWORD=<db_pass>
   ANDROMEDA_AUTH_SERV_URL=https://auth.example.com/andromeda
   ```

   `CORS_ALLOWED_ORIGIN_PATTERNS` controls the allowed browser origins for cross-origin requests.
    See `.env.example` for the full public-preview template, including optional test-database variables.

3. **Build and Run**
   ```bash
   # Build the package
   ./mvnw clean package

   # Run the application
    java -jar target/nebula-rest-api-5.4.0-PUBLIC-PREVIEW.war
   ```

### Run tests

```bash
./mvnw test
```

---

## 🚀 Verification

After starting, the server is available at `http://localhost:8080`. You can verify the setup by calling:

```bash
curl http://localhost:8080/api/v1/hello
```

You can also verify the public API and docs via:

```bash
curl http://localhost:8080/api/v1/version
curl http://localhost:8080/api/actuator/health
curl http://localhost:8080/nebula/v3/api-docs
```

---

## 📄 License

This project is licensed under the **Apache License 2.0**. See the `LICENSE` file in the repository root.

---

## 👤 Author

**Szymon Derleta**  
GitHub: [@szymonderleta](https://github.com/szymonderleta)

## 🏠 Project

Organization: [@MilkyWay-HomeLabs](https://github.com/MilkyWay-HomeLabs)  
Repository: [Nebula-Rest-Api](https://github.com/MilkyWay-HomeLabs/nebula-rest-api-public)
