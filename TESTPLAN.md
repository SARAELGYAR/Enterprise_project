# Enterprise Test Plan

All tests run in CI via `./gradlew build` (`.github/workflows/ci.yml`).

| Requirement | Marks | Test class | Test method |
|-------------|-------|------------|-------------|
| Cross-tenant read | 4 | `TenantIsolationIntegrationTest` | `crossTenantRead_project_returns404` |
| Cross-tenant update | 4 | `TenantIsolationIntegrationTest` | `crossTenantUpdate_task_returns404` |
| Cross-tenant list | 4 | `TenantIsolationIntegrationTest` | `crossTenantList_projects_returnsEmptyList` |
| Missing token → 401 | 3 | `RbacIntegrationTest` | `missingToken_returns401` |
| Wrong role → 403 | 3 | `RbacIntegrationTest` | `wrongRole_returns403` |
| Admin allowed | 3 | `RbacIntegrationTest` | `adminAllowed_returns201` |
| Transaction rollback | 2 | `ProjectTransactionalServiceTest` | `createProjectWithInitialTask_failure_rollsBackProjectAndTask` |
| Concurrency / optimistic lock | 3 | `ConcurrencyIntegrationTest` | `concurrentIncrements_counterMatchesThreadCount` |
| Messaging idempotency | 2 | `MessagingReliabilityIntegrationTest` | `duplicateMessages_processedOnce_jobCompleted` |
| Actuator health + probes + correlation | 1 | `ObservabilityIntegrationTest` | `healthEndpoint_isAvailable`, `livenessAndReadinessEndpoints_areAvailable`, `correlationId_isEchoedInResponseHeader` |

## Isolation policy

Cross-tenant access returns **404** (not 403) so existence of other tenants' resources is not leaked.

## Run locally

```bash
./gradlew test --tests "com.workhub.integration.*" --tests "com.workhub.messaging.*" --tests "com.workhub.observability.*" --tests "com.workhub.service.ProjectTransactionalServiceTest"
```

Docker must be running for RabbitMQ Testcontainers.
