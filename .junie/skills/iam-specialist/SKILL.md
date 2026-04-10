---
name: "iam-specialist"
description: "Expertise in Identity and Access Management, authentication, and authorization. Use when implementing login flows, configuring JWT/OAuth2, managing roles and permissions, or integrating authentication into services."
---
# IAM Specialist

## When to Use
- Adding authentication or authorization to a new or existing service.
- Modifying login flows, JWT token handling, or OAuth2 configuration.
- Implementing or updating role-based access control (RBAC).
- Reviewing security of identity-related code.

## Project Authentication Architecture
- **`authentication-service`**: Central service handling user authentication, token issuance, and validation.
- **`authentication-library`**: Shared library providing authentication utilities for other services.
- **`spring-boot-starter-authentication`**: Auto-configured starter for quick authentication integration.
- **`permission-library`**: Shared library for RBAC permission checks.

## Integration Steps
1. Add `spring-boot-starter-authentication` as a dependency in the service's `pom.xml`.
2. Configure authentication properties in `application.yml`.
3. Use `permission-library` annotations or filters to enforce role-based access on endpoints.
4. Test authentication flows with both valid and invalid tokens.

## Authentication Standards
- Use JWT for stateless authentication between services.
- Tokens must be validated on every request; never trust client-provided claims without verification.
- Refresh tokens must have appropriate expiration and rotation policies.
- All inter-service communication must include authentication headers.

## Authorization Standards
- Implement RBAC using `permission-library` for all user-facing endpoints.
- Define roles and permissions at the service level; do not hardcode role checks.
- Use method-level security annotations where appropriate.

## Constraints
- Never store plaintext passwords; use bcrypt or equivalent hashing.
- Never log tokens, passwords, or session identifiers.
- Never expose internal authentication details in API error responses.
