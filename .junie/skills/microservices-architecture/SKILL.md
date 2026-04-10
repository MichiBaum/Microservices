---
name: "microservices-architecture"
description: "Principles and practices for maintaining a robust, independent, and scalable microservices ecosystem using Spring Cloud. Use when creating new services, modifying inter-service communication, updating shared libraries, or reviewing architectural decisions."
---
# Microservices Architecture

## When to Use
- Creating a new microservice or shared library.
- Modifying how services communicate (REST, events).
- Reviewing or changing the service discovery, gateway, or resilience patterns.
- Deciding where new code should live (service vs. library vs. starter).

## Architecture Overview
- **Service Discovery**: Netflix Eureka via `registry-service`. All services register on startup.
- **API Gateway**: Spring Cloud Gateway (WebMVC) in `gateway-service`. All external traffic routes through here.
- **Admin**: Spring Boot Admin in `admin-service` for monitoring service health.

## Service Independence Rules
- Services must NOT have compile-time dependencies on other services.
- Services communicate via REST (OpenFeign) or event-driven messaging only.
- No direct database sharing between services. Each service owns its data.

## Inter-service Communication
1. Use **OpenFeign** for synchronous REST calls between services.
2. Add timeouts and circuit breakers (`resilience4j`) for all external calls.
3. Prefer asynchronous communication where eventual consistency is acceptable.

## Shared Code Guidelines
- `*-library`: General-purpose shared code (e.g., `permission-library`, `authentication-library`, `usermanagement-library`).
- `spring-boot-starter-*`: Auto-configured Spring Boot starters (e.g., `spring-boot-starter-authentication`, `spring-boot-starter-cache`, `spring-boot-starter-discord`).
- Before creating new utility code, check if it belongs in an existing library or starter.

## Naming Conventions
- `*-service` for deployable microservices.
- `*-library` for shared libraries.
- `spring-boot-starter-*` for Spring Boot starters.

## Step-by-Step: Adding a New Service
1. Create a new Maven module named `<name>-service` in the project root.
2. Add it to the root `pom.xml` as a module.
3. Configure Eureka client registration in `application.yml`.
4. Add Flyway migrations in `src/main/resources/db/migration` if the service needs a database.
5. Add the service to `docker-compose.local.yml` for local development.
6. Add Kubernetes manifests in `kubernetes/` for deployment.
7. Update documentation in `docs/`.
