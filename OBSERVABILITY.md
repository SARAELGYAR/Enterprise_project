# Observability Verification Guide (Phase 2)

Evidence for WorkHub: actuator, metrics, readiness/liveness probes, correlation ID in logs.

## 1) Start the app

```powershell
.\gradlew.bat bootRun
```

(Base URL assumed: `http://localhost:8080`. Adjust if your port differs.)

## 2) Actuator health probes

Commands (PowerShell-safe `curl.exe`):

```powershell
curl.exe -s http://localhost:8080/actuator/health
curl.exe -s http://localhost:8080/actuator/health/liveness
curl.exe -s http://localhost:8080/actuator/health/readiness
```

**Actual response bodies** (verified against this codebase on a local run):

- **`/actuator/health`** — JSON with top-level `"status":"UP"` and components including **`livenessState`** and **`readinessState`** both `"UP"`, plus `"groups":["liveness","readiness"]`.

- **`/actuator/health/liveness`** — `{"status":"UP"}`

- **`/actuator/health/readiness`** — `{"status":"UP"}`

## 3) Metrics (Micrometer)

```powershell
curl.exe -s http://localhost:8080/actuator/metrics
curl.exe -s http://localhost:8080/actuator/metrics/jvm.memory.used
```

**Actual `/actuator/metrics` shape** — JSON object with a `names` array listing Micrometer meter names (e.g. `jvm.memory.used`, `http.server.requests`, `process.uptime`, HikariCP and Spring Security timers, etc.).

## 4) Prometheus scrape format

```powershell
curl.exe -s http://localhost:8080/actuator/prometheus
```

**Actual output** starts with Prometheus text exposition style, for example lines like:

```
# HELP spring_security_filterchains_header_before_total
# TYPE spring_security_filterchains_header_before_total counter
spring_security_filterchains_header_before_total{...} ...
```

(Long response; paging or `Select-Object -First N` is fine when demoing.)

## 5) Correlation ID — response header + log line

Custom ID (passed through):

```powershell
curl.exe -s -D - -H "X-Correlation-ID: demo-corr-123" http://localhost:8080/actuator/health
```

**Actual HTTP response headers** included:

```
HTTP/1.1 200
X-Correlation-ID: demo-corr-123
...
```

**Actual log lines** emitted by `CorrelationIdFilter` for that request (pattern from `application.yml`; your thread name and timestamps will differ):

```
... INFO  c.w.o.CorrelationIdFilter [corr=demo-corr-123] - Incoming request: method=GET, path=/actuator/health
... INFO  c.w.o.CorrelationIdFilter [corr=demo-corr-123] - Completed request: method=GET, path=/actuator/health, status=200
```

No client header (server generates UUID, echoes same in response):

```powershell
curl.exe -s -D - http://localhost:8080/actuator/health
```

Response includes `X-Correlation-ID: <uuid>`; logs show `[corr=<that-same-value>]` on incoming/completed lines.

## Implementation references

- Probe + exposure config: `src/main/resources/application.yml`
- Log pattern (includes `[corr=%X{correlationId}]`): `src/main/resources/application.yml` under `logging.pattern.console`
- Correlation filter + MDC: `src/main/java/com/workhub/observability/CorrelationIdFilter.java`
- Permitted actuator paths without JWT: `src/main/java/com/workhub/security/SecurityConfig.java`
