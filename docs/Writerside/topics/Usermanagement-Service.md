# Usermanagement Service

The Usermanagement Service is a microservice built with Kotlin and Spring Boot that handles user profile management and user-related operations across the microservices architecture. It provides a centralized system for managing user data and profiles.

## Key Features

- **User Profile Management**: Manages user profiles, including personal information, preferences, and settings.
- **User CRUD Operations**: Provides Create, Read, Update, and Delete operations for user accounts.
- **User Search and Filtering**: Enables searching and filtering users based on various criteria.
- **User Relationships**: Manages relationships between users (friends, followers, etc.).
- **User Preferences**: Stores and retrieves user preferences for customizing the user experience.
- **Profile Pictures**: Handles upload, storage, and retrieval of user profile pictures.

## Integration with Other Services

The Usermanagement Service integrates with other microservices through:

- The usermanagement-library, which provides common user management functionality
- RESTful APIs for user data access and manipulation
- Event-driven communication for user-related events

## Data Management

- Secure storage of user profile data
- Data validation and sanitization
- Privacy controls for user data
- Compliance with data protection regulations

## Technologies

- Kotlin
- Spring Boot
- Spring Data JPA
- RESTful APIs
- Docker for containerization
