---
name: "security-best-practices"
description: "Implementation of OWASP security practices, input validation, and secure communication. Use when reviewing code for security vulnerabilities, implementing input validation, or hardening service configurations."
---
# Security Best Practices

## When to Use
- Reviewing code for security vulnerabilities (injection, XSS, CSRF, etc.).
- Implementing input validation and sanitization.
- Configuring secure communication between services.
- Auditing dependencies for known vulnerabilities.

## OWASP Top 10 Compliance
- **Injection Prevention**: Use parameterized queries (JPA/Hibernate) for all database access. Never concatenate user input into queries.
- **Authentication**: Delegate to `authentication-service`; do not implement custom auth flows.
- **Sensitive Data Exposure**: Encrypt sensitive data at rest and in transit. Use HTTPS for all external communication.
- **XSS Prevention**: Sanitize all user-generated content before rendering. Angular's built-in sanitization handles most cases.
- **CSRF Protection**: Enable CSRF tokens for state-changing operations on user-facing endpoints.

## Input Validation Rules
1. Validate all user inputs at the controller layer using Bean Validation (`@Valid`, `@NotBlank`, `@Size`, etc.).
2. Apply business-level validation in the service layer.
3. Never trust client-side validation alone.
4. Reject unexpected fields; use explicit DTOs for request bodies.

## Secure Communication
- All inter-service calls must be authenticated (JWT tokens in headers).
- Use TLS/SSL for all external-facing endpoints.
- Do not expose internal service ports or debug endpoints in production.

## Dependency Security
- Regularly check dependencies for known CVEs.
- Keep Spring Boot and library versions up to date.
- Remove unused dependencies to reduce attack surface.

## Constraints
- Never log passwords, tokens, API keys, or PII.
- Never return stack traces or internal error details in API responses.
- Never disable security features (CSRF, CORS) without documented justification.
