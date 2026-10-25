# Gateway Service

The Gateway Service is a microservice built with Kotlin and Spring Boot (utilizing Spring Cloud Gateway Server MVC) that acts as the single edge entry point and reverse proxy for all external traffic entering the microservices ecosystem. It routes client requests to downstream microservices and internal observability dashboards based on domain names and host headers, while enforcing security, resiliency, load balancing, SEO management, and cross-cutting operational features.

## Core Intentions and Architectural Role

The primary purpose of the Gateway Service is to decouple external clients from internal microservice network topology and provide centralized cross-cutting capabilities at the edge of the platform.

### Centralized Request Routing and Domain Management
- **Single Edge Entry Point**: Incoming requests arrive at the Gateway Service, which inspects the host header and URI path to route traffic to the correct downstream microservice or internal tool.
- **Host-Based Routing**: Uses domain-based matching (such as subdomains for admin, authentication, usermanagement, chess, fitness, music, vpn, and observability tools) to isolate service access.
- **Domain Normalization and Redirects**: Handles platform-wide redirects, such as canonicalizing `www` subdomains to apex domain names and redirecting specialized brand or external domains.

### Cross-Cutting Security and Access Control
- **Edge Authentication**: Integrates with the shared authentication library to validate incoming bearer tokens and evaluate stateless user security contexts before requests reach internal services.
- **Permission Enforcement**: Protects restricted routes (such as internal monitoring dashboards or administrative endpoints) by requiring specific platform permissions prior to routing.
- **Global CORS Management**: Enforces centralized Cross-Origin Resource Sharing policies across all microservice entry points.

### Fault Tolerance and Traffic Resilience
- **Resilience4J Circuit Breakers**: Isolates downstream failures by wrapping service routes in circuit breakers configured with call thresholds, failure rates, and execution time limits.
- **Graceful Fallbacks**: Returns standardized service-unavailable responses when downstream services fail or circuit breakers open, preventing cascading failures across the system.
- **Automated Cache Eviction**: Monitored circuit breaker state transitions trigger automatic eviction of load balancer service instance caches whenever a downstream service circuit opens.

### Dynamic Discovery and Load Balancing
- **Eureka Integration**: Registers as a client with the Registry Service to dynamically locate healthy instances of registered microservices without relying on hardcoded network locations.
- **Client-Side Load Balancing**: Distributes request traffic across healthy instances of downstream services using Spring Cloud LoadBalancer.
- **Active Health Checking**: Periodically probes registered service instances using liveness health indicators to prune unresponsive instances from routing pools.

### SEO and Crawler Management
- **Dynamic Robots.txt**: Dynamically generates robot instructions for web crawlers based on configurable user-agent rules and sitemap references.
- **Dynamic Sitemap.xml Generation**: Dynamically constructs sitemap documents by combining static site routes with real-time data locations fetched concurrently from downstream services using Java Virtual Threads.

### Observability and Operational Control
- **Distributed Tracing and Metrics**: Uses OpenTelemetry tracing with W3C propagation and Micrometer observations to trace edge requests end-to-end across downstream microservices.
- **Custom Operator Endpoints**: Exposes custom administrative management endpoints (such as discovery cache refresh endpoints) to allow operators to manage routing state without service restarts.
- **Discord Bot Command Integration**: Listens for administrative system commands (such as shutdown or reboot requests) issued through Discord integrations.

## How It Works

### Request Lifecycle and Host-Based Routing
1. **Request Ingestion**: An incoming HTTP request is received by the Spring Web MVC servlet container (Undertow).
2. **Special Rule Processing**: High-priority routing rules check for special host matching (such as redirecting `www` subdomains to apex domains or domain-level redirects) and return immediate redirect responses.
3. **SEO Route Handling**: Requests for `/robots.txt` or `/sitemap.xml` are intercepted by dedicated controllers that dynamically assemble and return plain text or XML responses.
4. **Service Matching**: Functional router configurations match the request host header against registered service patterns (such as admin, chess, music, fitness, usermanagement, authentication, vpn, or observability tools like Grafana, Jaeger, and Prometheus).
5. **Catch-All Handling**: Any requests matching the root domain that are not captured by specific subdomains fall through to the main Website Service.

### Client-Side Load Balancing and Instance Resolution
1. **Registry Lookup**: The Gateway Service queries the Eureka Registry Service to obtain available instances for the target service identifier.
2. **Health Filter Validation**: Spring Cloud LoadBalancer filters the instance list by executing active liveness health checks against cached instance endpoints.
3. **Fallback Resolution**: If active health checks return no healthy instances, a fallback supplier drops back to the last known discovery list to ensure availability during transient network probes.
4. **Target URI Mapping**: The request is rewritten to target the selected service instance host and port.

### Circuit Breaker Execution and Resilience
1. **Resilience4J Protection**: Service calls are wrapped within a Resilience4J circuit breaker instance specific to that service.
2. **Sliding Window Monitoring**: The circuit breaker evaluates request call volumes, failure percentages, and response execution durations using a sliding window.
3. **Open State Transition**: If failure thresholds are exceeded, the circuit opens. The circuit breaker event listener intercepts the state change and immediately evicts cached load balancer instances for that service.
4. **Fallback Response**: Subsequent requests to an open circuit bypass downstream execution and return an immediate service-unavailable response.

### Security Evaluation and Request Proxying
1. **Stateless Filter Execution**: Requests pass through the Servlet Authentication Filter, which extracts and validates security tokens from the HTTP Authorization header.
2. **Permission Verification**: For protected routes (such as Grafana, Jaeger, or Prometheus), the gateway verifies that the authenticated user possesses required authority flags before granting passage.
3. **Unauthenticated Denial**: Requests failing security or permission checks receive an HTTP 401 Unauthorized or HTTP 403 Forbidden status directly from the edge.
4. **Downstream Execution**: Validated requests are forwarded to the target service instance with trace context headers attached.

### Concurrent Sitemap Generation
1. **Static Route Assembly**: Static URLs defined in configuration properties are added to the sitemap XML builder.
2. **Parallel Downstream Fetching**: For dynamic sitemap paths (such as chess openings and chess events), the Gateway Service dispatches concurrent fetch tasks using Java Virtual Thread task executors.
3. **Feign Client Execution**: Dedicated Feign HTTP clients query downstream microservices (such as the Chess Service) to retrieve current data identifiers.
4. **XML Aggregation**: Results from all parallel virtual thread tasks are collected, formatted into XML URL entries, and returned as a unified sitemap document.

### Operational Management and Health Monitoring
1. **Actuator Health Probes**: Exposes liveness and readiness health probes for container orchestrators.
2. **Discovery Cache Refresh Endpoint**: Operators can invoke a custom operation on the discovery refresh endpoint to manually flush load balancer caches across all registered services.
3. **Observability Telemetry**: Trace contexts and high-cardinality observation metrics (including host headers and fetch locations) are exported to OpenTelemetry/Jaeger and Prometheus.

## Routed Microservices and Infrastructure Tools

The Gateway Service manages external entry points for the following microservices and administrative tools:

- **Website Service**: Serves the main website and frontend application for primary domain requests.
- **Admin Service**: Receives administrative traffic for cluster inspection, monitoring, and management.
- **Authentication Service**: Handles user login, account registration, and token validation requests.
- **User Management Service**: Handles user profile administration and account operations.
- **Chess Service**: Routes chess gameplay, openings database, and tournament management requests.
- **Fitness Service**: Routes workout and physical activity tracking endpoints.
- **Music Service**: Handles music catalog and audio streaming queries.
- **VPN Service**: Manages secure network tunneling and VPN configuration endpoints.
- **Registry Service**: Exposes service discovery administrative interfaces.
- **Grafana Dashboard**: Protected endpoint routing traffic to the Grafana metrics visualization UI.
- **Jaeger Tracing**: Protected endpoint routing traffic to the Jaeger distributed tracing UI.
- **Prometheus Monitoring**: Protected endpoint routing traffic to the Prometheus metrics server.

## Technologies and Dependencies

- **Kotlin & Java Virtual Threads**: Core language and concurrent execution thread model for non-blocking task execution.
- **Spring Boot & Spring Web MVC**: Base application framework and Undertow web server.
- **Spring Cloud Gateway Server MVC**: Functional HTTP routing framework built on Spring MVC.
- **Spring Cloud Netflix Eureka Client**: Service registration and dynamic discovery client.
- **Spring Cloud LoadBalancer**: Client-side load balancing with custom health-checking suppliers.
- **Resilience4J & Spring Cloud CircuitBreaker**: Circuit breaker fault tolerance, rate limiting, and time limiting.
- **Spring Security & Authentication Library**: Centralized security filter and RBAC permission verification.
- **OpenFeign**: Declarative REST client for inter-service communication during sitemap generation.
- **Micrometer Observation & OpenTelemetry**: Metrics collection and W3C distributed tracing context propagation.
- **Spring Boot Starter Discord**: Embedded Discord bot integration for administrative control commands.
