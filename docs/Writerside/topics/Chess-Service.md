# Chess Service

The Chess Service is a microservice built with Kotlin and Spring Boot that provides functionality for managing chess-related data and operations. It is part of the larger microservices architecture.

## Components

The Chess Service consists of several key components:

### Application Logic (app)

- **Account Management**: Handles user accounts and authentication for chess players.
- **Chess Engine Integration**: Interfaces with chess engines for analysis and move generation.
- **Event Management**: Manages chess events such as tournaments and matches.
- **Event Category Management**: Organizes events into categories.
- **Game Management**: Handles chess games, including moves, positions, and game states.
- **Opening Management**: Manages chess openings, variations, and move sequences.
- **Person Management**: Handles information about chess players and other individuals.

### APIs (apis)

Contains API interfaces and clients for communicating with other services or external systems.

### Database (database)

Manages the persistence of chess-related data, including entities and repositories for:
- Events
- Event Categories
- Games
- Openings
- Players
- Moves

### Security (security)

Handles authentication, authorization, and other security-related functionality specific to the Chess Service.

## REST Endpoints

The Chess Service exposes several REST endpoints for interacting with the service, including:

- Endpoints for managing chess openings and moves
- Endpoints for managing chess games
- Endpoints for managing chess events and categories
- Endpoints for player information

## Technologies

- Kotlin
- Spring Boot
- Spring Data JPA
- RESTful APIs
- Docker for containerization
