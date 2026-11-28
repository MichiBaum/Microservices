---
name: "observability-mastery"
description: "Expertise in distributed tracing, metrics, and monitoring using Grafana, Prometheus, and Jaeger. Use when configuring monitoring, adding metrics endpoints, setting up tracing, or creating dashboards."
---
# Observability Mastery

## When to Use
- Adding or configuring metrics endpoints for a service.
- Setting up or modifying distributed tracing across services.
- Creating or updating Grafana dashboards.
- Troubleshooting performance issues using metrics and traces.

## Observability Stack
- **Prometheus**: Metrics collection and alerting. Configuration in `grafana/` directory.
- **Grafana**: Dashboards and visualization. Configuration in `grafana/` directory.
- **Jaeger**: Distributed tracing. Configuration in `jaeger/` directory.
- **Spring Boot Actuator**: Exposes metrics and health endpoints for each service.

## Metrics Standards
- All services must expose metrics via Spring Boot Actuator (`/actuator/prometheus`).
- Use Micrometer for custom metrics (counters, gauges, timers, distribution summaries).
- Name metrics following the `<service>.<domain>.<action>` convention.
- Add meaningful tags to metrics for filtering and aggregation.

## Distributed Tracing
- All inter-service calls must propagate trace context headers.
- Use Micrometer Tracing / OpenTelemetry for automatic trace propagation across services.
- Add custom spans for significant business operations.
- Ensure trace IDs appear in log output for correlation.
- **Trace Filter & Path Exclusions**: When excluding Actuator health polls or Eureka heartbeats from trace generation, use observation predicates (e.g. implementing `ObservationPredicate`) or `management.observations.http.server.requests.ignore-paths`. Do not strip security filters or broad `spring.security.*` attributes.

## Alerting & Dashboards
- Define alerts for critical metrics (error rates, latency percentiles, resource usage).
- Create service-specific Grafana dashboards for key business and technical metrics.
- Include RED metrics (Rate, Errors, Duration) for all service dashboards.

## Constraints
- Do not disable Actuator endpoints in production; restrict access via security configuration instead.
- Do not log at DEBUG level in production; use metrics and traces for observability.
