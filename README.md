# WorkHub — Multi-Tenant SaaS Backend

Spring Boot 3 backend for project/task management with JWT auth, RBAC, tenant isolation, RabbitMQ async reports, and observability.

## Prerequisites

- Java 17
- Docker Desktop (for Compose and integration tests with Testcontainers)

## Run locally (Gradle)

```bash
./gradlew bootRun
```

Default Postgres connection is configured in `application.yml`. For H2-free local dev, start Postgres via Compose first:

```bash
docker compose up -d postgres rabbitmq
```

## Run with Docker Compose (full stack)

```bash
docker compose up --build
```

- API: http://localhost:8080
- Health: http://localhost:8080/actuator/health
- RabbitMQ UI: http://localhost:15672 (guest/guest)

### Demo login (seeded)

| Email | Password | Role |
|-------|----------|------|
| admin@tenant1.com | password123 | TENANT_ADMIN |
| member@tenant1.com | password123 | TENANT_USER |

```bash
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@tenant1.com","password":"password123"}'
```

## Tests

```bash
./gradlew test
```

Requires Docker for Testcontainers (RabbitMQ). See `TESTPLAN.md` for required enterprise integration tests.

## Documentation

- `DEPLOYMENT.md` — Compose, Kubernetes, Terraform
- `TENANT-ISOLATION-PROOF.md` — isolation evidence
- `OBSERVABILITY.md` — actuator endpoints
- `TESTPLAN.md` — mandatory test matrix
