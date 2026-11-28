# Usermanagement library

The **Usermanagement Library** provides common DTOs, endpoint interfaces, and a Feign client for interacting with the `usermanagement-service`.

## Components

### Endpoints and Feign Client
- **`UserManagementEndpoints`**: Defines RESTful endpoints for user profile operations:
  - `POST /api/users`: Creates a new user profile using `CreateUserDto` and returns `UserDetailsDto`.
  - `GET /api/users/{id}`: Retrieves user details and granted permissions for a given user ID.
  - `DELETE /api/users/{id}`: Deletes a user profile by ID.
- **`UsermanagementClient`**: Spring Cloud OpenFeign client implementing `UserManagementEndpoints` for inter-service communication with `usermanagement-service`.

### Data Transfer Objects (DTOs)
- **`UserDetailsDto`**: Contains user profile data and permissions:
  - `id: String`: Unique user identifier.
  - `permissions: Set<String>`: Set of permissions assigned to the user.
- **`CreateUserDto`**: Payload for user creation containing `email: String`.
- **`CreateUserDtoValidator`**: Helper to validate `CreateUserDto` payload.

