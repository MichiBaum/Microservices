# Libraries

Shared functionality across the microservices repository is split into general-purpose libraries (`*-library`) and Spring Boot starters (`spring-boot-starter-*`), preserving zero compile-time coupling between deployable microservices.

## Shared Libraries (`*-library`)

- **`permission-library`**: Defines the shared `Permissions` RBAC enum enforced across services and gateway routes.
- **`authentication-library`**: Provides JWT validation, `ServletAuthenticationFilter`, and `@PublicEndpoint` scanning utilities.
- **`usermanagement-library`**: Shared DTOs and Feign client contracts for user management integration.
- **`chess-play-library`**: Pure domain library for chess move generation, FEN/PGN parsing, and board validation.

## Spring Boot Starters (`spring-boot-starter-*`)

- **`spring-boot-starter-authentication`**: Auto-configures Spring Security JWT + Basic Auth (`AdminServiceCredentials`) and public endpoint resolution.
- **`spring-boot-starter-cache`**: Auto-configures Caffeine caching and `@ProjectCacheable` TTL support.
- **`spring-boot-starter-discord`**: Auto-configures Discord bot integration, slash commands, and log channel forwarding.
- **`spring-boot-starter-observability`**: Auto-configures Micrometer `@Observed` aspects and context-propagating task decorators.
- **`spring-boot-starter-gateway-discovery`**: Enables microservices to self-publish their gateway routes, circuit breakers, host-scoped CORS origins, `robots.txt` rules, and `sitemap.xml` URLs to `registry-service` via Eureka metadata.

## Onboarding a Service with `spring-boot-starter-gateway-discovery`

### 1. Add the Maven Dependency

```xml
<dependency>
    <groupId>com.michibaum</groupId>
    <artifactId>spring-boot-starter-gateway-discovery</artifactId>
</dependency>
```

### 2. Configure `gateway.publish.*` in `application.yml`

```yaml
gateway:
  publish:
    enabled: true # default: true (set to false on gateway-service itself)
    routes:
      - id: "myservice-main"           # optional; defaults to <serviceId>-<index>
        hosts:
          - "myservice.michibaum.*"
        paths: []                      # optional path predicates (e.g., ["/api/**"])
        order: 0                       # 0 for subdomain services; 1000 for website-service catch-all
        required-permissions: []       # optional Permissions enum values (e.g., [ADMIN_SERVICE])
        circuit-breaker:               # optional per-route Resilience4j overrides
          timeout-seconds: 10
          sliding-window-size: 50
          minimum-number-of-calls: 20
          failure-rate-threshold: 50.0
          wait-duration-in-open-state-seconds: 10
          permitted-number-of-calls-in-half-open-state: 10
          slow-call-rate-threshold: 50.0
          slow-call-duration-threshold-seconds: 5
    cors:
      allowed-origin-patterns:         # merged with gateway baseline michibaum.* origins for this host
        - "https://partner.example.com"
      allowed-methods: ["*"]
      allowed-headers: ["*"]
      allow-credentials: true
    sitemap:
      static-locations:
        - "/myservice"
        - "/myservice/overview"
    robots:
      crawlers:
        - user-agent: "*"
          allowed:
            - "/myservice"
          disallowed:
            - "/myservice/internal"
```

### 3. Expose Dynamic Sitemap URLs via `SitemapUrlProvider` (Optional)

When a service needs database-driven URLs in `/sitemap.xml` (such as `chess-service` events and openings), register one or more `SitemapUrlProvider` beans. The starter automatically exposes `GET /actuator/sitemap` (secured by `Permissions.ADMIN_SERVICE`) and advertises `/actuator/sitemap` in Eureka metadata:

```kotlin
@Service
class MyServiceSitemapUrlProvider(
    private val itemRepository: ItemRepository
) : SitemapUrlProvider {

    @Transactional(readOnly = true)
    override fun provideUrls(): List<String> {
        return itemRepository.findAllIds().map { id -> "/myservice/items/$id" }
    }
}
```

Use lightweight ID projections (`SELECT i.id FROM Item i`) rather than loading full JPA entity graphs so sitemap generation remains fast and memory-efficient.