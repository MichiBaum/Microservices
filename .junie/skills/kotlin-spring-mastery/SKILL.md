---
name: "kotlin-spring-mastery"
description: "Expertise in Kotlin 2.3+, Spring Boot 4.0+, and Java 25 development. Use when writing or reviewing backend Kotlin code, configuring Spring Boot services, or managing Maven dependencies."
---
# Kotlin & Spring Mastery

## When to Use
- Writing or modifying backend service code in Kotlin.
- Configuring Spring Boot applications, profiles, or properties.
- Adding or updating Maven dependencies.
- Reviewing Kotlin code for idiomatic patterns and best practices.

## Technology Stack
- **Kotlin 2.3+** as the primary language for all backend services.
- **Java 25** as the JVM target.
- **Spring Boot 4.0+** for application framework.
- **Spring Cloud 2025.x** for distributed systems patterns.
- **Maven** for build and dependency management.

## Kotlin Coding Standards
- Use null-safe operators (`?.`, `?:`, `let`, `also`) instead of `!!`.
- Prefer `data class` for DTOs and value objects.
- Use `sealed class` / `sealed interface` for restricted type hierarchies.
- Leverage extension functions for clean, readable utility code.
- Use `kotlinx-coroutines` for asynchronous programming where appropriate.

## Spring Boot Conventions
- Place configuration in `application.yml` (not `.properties`).
- Use constructor injection (Kotlin primary constructors) instead of field injection.
- Annotate REST controllers with `@RestController` and use `@RequestMapping` for base paths.
- Use `@ConfigurationProperties` for type-safe configuration binding.
- Keep `@Service`, `@Repository`, and `@Controller` layers cleanly separated.

## Dependency Management
- All dependency versions are managed in the root `pom.xml`.
- Do not add version numbers in child module POMs unless overriding is explicitly needed.
- Check existing libraries and starters before adding new external dependencies.

## Constraints
- Do not use Java-style patterns when Kotlin equivalents exist (e.g., use `when` instead of `switch`).
- Do not use `var` when `val` suffices.
- Do not suppress warnings without a documented reason.
