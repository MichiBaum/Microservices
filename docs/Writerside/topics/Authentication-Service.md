# Authentication Service

The Authentication Service is a microservice built with Kotlin and Spring Boot that handles user authentication and authorization for the entire microservices architecture. It provides secure access to protected resources across all services.

## Key Features

- **User Authentication**: Verifies user identities through username/password, OAuth, or other authentication methods.
- **Token Management**: Issues, validates, and refreshes JWT (JSON Web Tokens) or other authentication tokens.
- **Authorization**: Manages user roles and permissions to control access to resources.
- **User Registration**: Handles the creation of new user accounts.
- **Password Management**: Manages password resets, changes, and security policies.

## Integration with Other Services

The Authentication Service integrates with other microservices through:

- The authentication-library, which provides common authentication functionality
- The spring-boot-starter-authentication, which simplifies integration with Spring Boot applications
- RESTful APIs for authentication and user management

## Credential Verification & Token Issuance

- **Local Authority**: User credentials (`username` and `password_hash`) are stored and verified exclusively within `authentication-service` (`user_credentials` table).
- **Decoupled Permissions**: Upon successful local password verification, user permissions are fetched from `usermanagement-service` via `UsermanagementClient.getUserDetailsById`. If the user profile is missing in `usermanagement-service`, authentication safely returns HTTP 401 Unauthorized.
- **Timing Attack Mitigation**: When no user credentials exist for a requested username, a dummy BCrypt comparison is executed against a constant precomputed hash to prevent username enumeration timing oracles.
- **Token Signing**: Signed JWS tokens are issued with the local credential username as subject and the resolved permissions as claims. No fallback RPC calls are made on invalid or missing credentials.

## Security Measures

- Secure password storage using bcrypt or similar hashing algorithms
- Protection against common security threats (CSRF, XSS, etc.)
- Rate limiting to prevent brute force attacks
- Audit logging for security events

## Technologies

- Kotlin
- Spring Boot
- Spring Security
- JWT for token-based authentication
- RESTful APIs
- Docker for containerization
