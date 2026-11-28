---
name: "testing-polyglot"
description: "Proficiency in backend and frontend testing frameworks including JUnit 5, MockK, Testcontainers, ArchUnit, Jasmine, and Karma. Use when writing unit tests, integration tests, or architectural tests for any module."
---
# Testing Mastery

## When to Use
- Writing unit tests for new business logic.
- Creating integration tests for service endpoints or database interactions.
- Adding architectural tests to enforce module boundaries.
- Running or debugging existing test suites.

## Backend Testing Stack
- **JUnit 5**: Test framework for all backend tests.
- **AssertJ**: Fluent assertion library (preferred over JUnit assertions).
- **MockK**: Kotlin-idiomatic mocking library (use instead of Mockito).
- **Testcontainers**: Real database and infrastructure containers for integration tests.
- **ArchUnit**: Architectural constraint verification (tests in `archunittests/` module).

## Frontend Testing Stack
- **Jasmine**: Test framework for Angular unit tests.
- **Karma**: Test runner for Angular tests.
- Run with `npm test` from the `website/` directory.

## Test Writing Standards
1. Follow the **AAA pattern**: Arrange, Act, Assert.
2. One logical assertion per test method (multiple related AssertJ assertions are fine).
3. Use descriptive test method names: `should <expected behavior> when <condition>`.
4. Keep tests independent; no shared mutable state between tests.
5. Use `@BeforeEach` for common setup, not `@BeforeAll` (unless truly expensive).

## Unit Test Guidelines
- Mock external dependencies using MockK (`mockk<T>()`, `every { }`, `verify { }`).
- Test edge cases: null inputs, empty collections, boundary values.
- Test both success and failure paths.

## Integration Test Guidelines
- Use `@SpringBootTest` with Testcontainers for database-dependent tests.
- Use `@Testcontainers` and `@Container` annotations for container lifecycle management.
- Test actual HTTP endpoints using `TestRestTemplate` or `WebTestClient`.
- Clean up test data after each test to avoid interference.

## Performance Testing
- Use `oha`, `JMeter`, or Spring Boot Actuator for benchmarking.
- Performance test configuration is available in `Microservices.jmx`.

## Constraints
- All new business logic must have corresponding unit tests.
- Integration tests must use Testcontainers, not in-memory databases (e.g., H2).
- Do not use `@Disabled` or `@Ignored` to skip failing tests without a documented reason.
