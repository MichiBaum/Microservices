# Decisions

## Migration from Zipkin to Jaeger with OpenTelemetry

This document outlines the decision and implementation details for migrating the project's distributed tracing infrastructure from Zipkin to Jaeger using the OpenTelemetry Protocol (OTLP).

### Difference between Zipkin, Jaeger, and Jaeger with OTLP

*   **Zipkin:** Historically one of the first distributed tracing systems. It uses its own B3 propagation format and a relatively simple architecture. While reliable, its UI is less feature-rich than modern alternatives.
*   **Jaeger:** A CNCF-graduated project designed for cloud-native environments. It offers a highly modular architecture (Collectors, Query, Ingestor) and a more advanced UI that provides better dependency visualization and search capabilities.
*   **Jaeger with OTLP:** OpenTelemetry (OTEL) is the vendor-neutral industry standard for observability. By using OTLP as the communication protocol, we decouple the microservices from a specific backend. Jaeger natively supports OTLP, allowing us to push traces using a standardized format.

### Reasons to change from Zipkin to Jaeger

1.  **Industry Standardization:** OpenTelemetry is the current industry standard. Adopting OTLP ensures the project follows modern best practices and remains vendor-agnostic.
2.  **Native Spring Boot Integration:** With Spring Boot 3.2+ and 4.x, the `spring-boot-starter-opentelemetry` provides first-class, native support for OTLP, reducing the complexity of custom configurations.
3.  **Superior UI and Analysis:** Jaeger provides more advanced tools for analyzing trace timelines, identifying bottlenecks, and visualizing the service graph.
4.  **Improved Scalability and Reliability:** The migration included moving to Elasticsearch for persistent storage, which is more robust and scalable than basic in-memory or simpler storage backends.
5.  **Unified Observability:** Using the OTLP protocol paves the way for a unified collection of logs, metrics, and traces through a single pipeline.

### What needed to be changed

The migration involved significant changes across the entire codebase:

*   **Dependency Management:** In all microservices (e.g., `admin-service`, `gateway-service`, `authentication-service`), the `micrometer-tracing-bridge-otel` was removed in favor of `spring-boot-starter-opentelemetry`.
*   **Application Configuration:** Properties were refactored from Zipkin-specific settings to native OTLP properties. This included setting `management.opentelemetry.tracing.export.otlp.endpoint` to `http://localhost:4318/v1/traces` (for local dev) or `http://jaeger:4318/v1/traces` (for production).
*   **Infrastructure (Docker Compose):** The Zipkin service was replaced with a `jaeger` service (using the `jaegertracing/all-in-one` image) and a `jaeger-storage` service (using Elasticsearch).
*   **Infrastructure (Kubernetes):**
    *   `zipkin.yaml` was deleted.
    *   `jaeger.yaml` was created, defining the Jaeger deployment, Elasticsearch persistent volumes, services, and configuration maps.
    *   Reliability features like `livenessProbe`, `readinessProbe`, and resource `requests`/`limits` were added.
*   **Externalized Configuration:** Jaeger settings were moved to dedicated configuration files (`jaeger/jaeger.yml` and `jaeger/ui-config.json`) to improve maintainability.
*   **Gateway Integration:** The Traefik ingress controller configuration was updated to export traces directly to Jaeger using OTLP.

### Future development improvements

The move to OTLP and Jaeger enables several future enhancements:

*   **Performance Optimization:** We can easily switch from OTLP/HTTP (port 4318) to OTLP/gRPC (port 4317) to reduce overhead and improve trace ingestion performance.
*   **Observability Correlations:** The standardized format makes it easier to correlate traces with logs and metrics in tools like Grafana, providing a "single pane of glass" view.
*   **Dynamic Sampling:** We can implement remote sampling in Jaeger to dynamically control the volume of traces collected without needing to redeploy services.
*   **Infrastructure Scaling:** As the project grows, the Jaeger architecture can be scaled out from "all-in-one" to individual components (collector, query, etc.) thanks to the modular nature of the setup.