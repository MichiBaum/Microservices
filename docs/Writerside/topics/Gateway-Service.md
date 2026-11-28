# Gateway Service

The Gateway Service (`gateway-service`) is a Spring Boot WebMVC API gateway (`spring-cloud-starter-gateway-server-webmvc`) that serves as the single external entry point for the microservices ecosystem. Instead of hardcoding downstream microservice knowledge, `gateway-service` dynamically discovers and hot-reloads routes, circuit breakers, host-scoped CORS policies, `robots.txt` crawler rules, and `sitemap.xml` URLs published by microservices to `registry-service` (Netflix Eureka).

## Dynamic Discovery Architecture

1. **Eureka Metadata Synchronization (`ServiceGatewayConfigStore`)**:
   - Listens to `ApplicationReadyEvent`, Spring Cloud `HeartbeatEvent`, and a scheduled safety sync to inspect Eureka instance `metadataMap` entries (`michibaum.gateway.schema-version`, `michibaum.gateway.hash`, `michibaum.gateway.config`).
   - Rebuilds routing and CORS snapshots only when the aggregate SHA-256 hash map changes.
   - **Last-Known-Good Outage Retention**: If all instances of a previously discovered service go offline, `ServiceGatewayConfigStore` retains its last-known route snapshot so requests to that subdomain return `503 Service Unavailable` via its circuit breaker instead of falling through to the `michibaum.*` catch-all route.
   - **Manual Refresh & Eviction**: Operators can invoke `POST /actuator/discoveryRefresh` (protected by `Permissions.ADMIN_SERVICE`) to clear `LoadBalancerCacheManager` caches, resynchronize `ServiceGatewayConfigStore`, and purge decommissioned services (`POST /actuator/discoveryRefresh/sync` retains offline snapshots).

2. **Lock-Free Atomic Routing (`DynamicGatewayRouter`)**:
   - Exposes a delegating `RouterFunction<ServerResponse>` backed by an `AtomicReference<RouterFunction<ServerResponse>>`.
   - Evaluates routes in strict priority order:
     1. Declarative domain redirects and `204 No Content` hosts (`gateway.static-routes.redirects`, e.g., `www.michibaum.{tld}`, `babymetal.ch`, `hanabie.ch`, `baumberger-software.*`, `einsiedeln.*`).
     2. Gateway-local crawler endpoints (`GET /robots.txt`, `GET /sitemap.xml`).
     3. Declarative external infrastructure routes (`gateway.static-routes.external-services`: `jaeger`, `grafana`, `prometheus`) and discovered Eureka routes sorted ascending by `order` (`order = 0` for specific subdomain services; `order = 1000` for the `website-service` `michibaum.*` catch-all), with host-specificity tie-breaking.
   - Enforces route-level `requiredPermissions` via `ServletAuthenticationFilter` (`401 Unauthorized` when missing) and isolates each service behind a Resilience4j circuit breaker (`<serviceId>-circuit-breaker`) with automatic load-balancer cache eviction on `OPEN` transitions.

3. **Host-Scoped CORS (`DynamicGatewayCorsConfigurationSource`)**:
   - Wired into `SecurityConfiguration`, matching each request's `Host` header (including `OPTIONS` preflight) against discovered service host patterns.
   - Merges shared baseline domains (`cors.base-allowed-origin-patterns` in `application-dev.yml` and `application-prod.yml`) with only the matched service's published `allowedOriginPatterns`.

4. **Dynamic `robots.txt` & `sitemap.xml` Aggregation**:
   - `RobotsTxtService` deterministically merges gateway baseline crawler rules (`robots-txt.*`) with per-service `RobotsMetadata` grouped by `userAgent`.
   - `SitemapXmlService` combines static paths from `sitemap-xml.locations` and discovered `sitemap.staticLocations`, and fans out concurrently on virtual threads (`VirtualThreadTaskExecutor`) via `LoadBalancerClient` and `RestClient` (authenticated with `AdminServiceCredentials` Basic Auth) to every service advertising `/actuator/sitemap`. Downstream timeouts or `5xx` errors degrade gracefully by logging a warning and returning all remaining valid URLs.

## Declarative Non-Eureka Routes (`application.yml`)

External non-Spring tools and domain redirects are configured declaratively in `gateway-service/src/main/resources/application.yml`:

```yaml
gateway:
  publish:
    enabled: false
  static-routes:
    redirects:
      - hosts: ["www.michibaum.{tld}"]
        status: PERMANENT_REDIRECT
        scheme-host-template: "{scheme}://michibaum.{tld}"
        preserve-path-and-query: true
    external-services:
      - id: "jaeger-service"
        hosts: ["jaeger.michibaum.*"]
        uri: ${management.services.jaeger-url}
        order: 0
        required-permissions: [ADMIN_SERVICE]
```

## Technologies

- Spring Boot 4 & Kotlin
- Spring Cloud Gateway Server WebMVC
- Spring Cloud LoadBalancer & Netflix Eureka Client
- Resilience4j Circuit Breaker & TimeLimiter
- Spring Security & `spring-boot-starter-gateway-discovery`
