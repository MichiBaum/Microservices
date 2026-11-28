# Registry Service

The Registry Service (`registry-service`) is a stateless Spring Boot Netflix Eureka Server (`@EnableEurekaServer`) that provides service registration, discovery, and gateway configuration transport across the microservices ecosystem.

## Key Features

- **Service Registration & Discovery**: Microservices register on startup and discover peer instances dynamically without hardcoded hostnames or ports.
- **Gateway Metadata Transport**: Carries each service's self-published gateway configuration (`michibaum.gateway.schema-version`, `michibaum.gateway.hash`, `michibaum.gateway.config`) inside Eureka's native instance `metadataMap`, requiring zero custom database tables or custom REST endpoints on `registry-service`.
- **Health & Eviction**: Tracks heartbeats from registered instances and evicts unhealthy instances automatically.
- **Self-Registration in Dev and Kubernetes Prod**: Registers itself (`registry.michibaum.*`) with its own local Eureka server so `gateway-service` dynamically discovers and load-balances `registry-service` alongside all other microservices.

## How Gateway Metadata Transport Works

1. Downstream services include `spring-boot-starter-gateway-discovery` and declare `gateway.publish.*` in `application.yml`.
2. Before Eureka registration, `EurekaGatewayMetadataBeanPostProcessor` serializes the service's routes, CORS origins, sitemap paths, and `robots.txt` rules into `eureka.instance.metadata-map` along with a SHA-256 content hash.
3. `gateway-service` polls `registry-service` via `DiscoveryClient`, compares `michibaum.gateway.hash` digests, and atomically hot-reloads routes, circuit breakers, and CORS rules when metadata changes.

## Production Loopback Self-Registration

In `registry-service/src/main/resources/application-prod.yml`, `registry-service` enables self-registration against its own loopback interface (`http://localhost:8761/eureka`) while keeping `eureka.instance.prefer-ip-address: true`:

```yaml
eureka:
  instance:
    prefer-ip-address: true
  client:
    register-with-eureka: true
    service-url:
      defaultZone: http://${ADMIN_SERVICE_USERNAME}:${ADMIN_SERVICE_PASSWORD}@localhost:8761/eureka
```

Targeting `localhost:8761` avoids Kubernetes Service DNS readiness deadlocks during pod startup while advertising the Pod IP in Eureka so `gateway-service` can route `registry.michibaum.*` traffic directly to healthy `registry-service` pods.

## Technologies

- Spring Boot 4 & Kotlin
- Spring Cloud Netflix Eureka Server & Client
- Spring Security (`ADMIN_SERVICE` HTTP Basic & JWT protection)
- `spring-boot-starter-gateway-discovery`
