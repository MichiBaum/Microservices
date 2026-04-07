---
name: "microservices-architecture"
description: "Principles and practices for maintaining a robust, independent, and scalable microservices ecosystem using Spring Cloud."
---
# 🏗️ Microservices Architecture Skills

## 🏗️ Microservices Ecosystem Mastery
- **Service Independence**: Services must be loosely coupled. Avoid direct dependency between services.
- **Service Discovery**: Uses **Netflix Eureka** via `registry-service`.
- **API Gateway**: Uses **Spring Cloud Gateway (WebMVC)** in `gateway-service`.
- **Inter-service Communication**:
    - Use **OpenFeign** for synchronous REST calls.
    - Prefer asynchronous communication where appropriate.
- **Shared Libraries**: 
    - `*-library`: General libraries (e.g., `permission-library`, `authentication-library`).
    - `spring-boot-starter-*`: Reusable Spring Boot starters (e.g., `spring-boot-starter-authentication`, `spring-boot-starter-discord`).
- **Naming**: services follow `*-service`, libraries follow `*-library` or `spring-boot-starter-*`.
- **Resilience**: Implement Circuit Breakers using `resilience4j` where needed.
