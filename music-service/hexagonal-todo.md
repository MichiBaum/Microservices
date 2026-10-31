# Hexagonal Architecture Refactoring Analysis & Plan for music-service

## Executive Summary & Value Proposition

Transforming `music-service` into Hexagonal Architecture (Ports and Adapters) optimizes long-term system utility by decoupling core music domain rules from infrastructure volatility (such as external API changes from Spotify, MusicBrainz, or Musixmatch and database schema shifts). This architectural refactoring maximizes developer productivity, reduces technical debt, speeds up automated test execution, and yields a high return on investment (ROI) across the service lifecycle.

---

## Current Architectural Bottlenecks & Utility Deficits

Analyzing the existing structure of `music-service` reveals several architectural coupling points:

- **Persistence Layer Entanglement**: Classes in `com.michibaum.music_service.database` combine Spring Data JPA annotations (`@Entity`, `@Table`) with business representations (`Artist`, `Album`, `Track`). Domain logic directly depends on database persistence models.
- **External Client Mixing**: Third-party integrations in `com.michibaum.music_service.apis` combine external DTOs, HTTP clients (`SpotifyApiImpl`, `MusicBrainzApiImpl`), web controllers (`SpotifyOAuthController`), and persistence repositories (`SpotifyOAuthCredentialsRepository`).
- **Direct Service Coupling**: Application services in `com.michibaum.music_service.app` depend directly on concrete Spring Data repositories and specific third-party HTTP clients instead of abstraction boundaries.

---

## Proposed Hexagonal Architecture Blueprint

To maximize modularity and operational efficiency, `music-service` should be reorganized into three distinct concentric layers: **Domain Core**, **Application (Ports)**, and **Infrastructure (Adapters)**.

```
com.michibaum.music_service/
├── domain/                         # Pure Domain Core (Framework-Free)
│   ├── model/                      # Pure Domain Entities & Value Objects (Artist, Album, Track, MusicLibrary)
│   └── exception/                  # Domain Exceptions
│
├── application/                    # Application Logic & Ports
│   ├── port/
│   │   ├── inbound/                # Driving Ports (Use Cases / Commands / Queries)
│   │   │   ├── GetArtistQuery.kt
│   │   │   ├── ManageMusicLibraryUseCase.kt
│   │   │   └── SpotifyOAuthUseCase.kt
│   │   └── outbound/               # Driven Ports (Abstract Interfaces)
│   │       ├── ArtistPersistencePort.kt
│   │       ├── SpotifyApiPort.kt
│   │       ├── MusicBrainzApiPort.kt
│   │       └── MusixmatchApiPort.kt
│   └── service/                    # Application Services implementing Inbound Ports
│       ├── ArtistApplicationService.kt
│       ├── MusicLibraryApplicationService.kt
│       └── SpotifyOAuthApplicationService.kt
│
└── infrastructure/                 # Adapters & Technical Frameworks
    ├── adapter/
    │   ├── inbound/                # Primary / Driving Adapters
    │   │   └── rest/               # REST Controllers, Request DTOs & Mappers
    │   │       ├── ArtistController.kt
    │   │       ├── AlbumController.kt
    │   │       └── SpotifyOAuthController.kt
    │   └── outbound/               # Secondary / Driven Adapters
    │       ├── persistence/        # Database Adapters (JPA Entities, Repositories, Mappers)
    │       │   ├── entity/         # JPA @Entity classes (ArtistEntity, AlbumEntity)
    │       │   ├── repository/     # Spring Data JPA Repositories
    │       │   └── adapter/        # ArtistPersistenceAdapter implementing ArtistPersistencePort
    │       └── external/           # External API Adapters
    │           ├── spotify/        # SpotifyWebAdapter implementing SpotifyApiPort
    │           ├── musicbrainz/    # MusicBrainzAdapter implementing MusicBrainzApiPort
    │           └── musixmatch/     # MusixmatchAdapter implementing MusixmatchApiPort
    └── config/                     # Spring Configuration (Security, Properties, Beans)
```

---

## Execution Strategy & Refactoring Steps

1. **Extract Pure Domain Models (`domain/model`)**:
   - Create framework-free data classes for `Artist`, `Album`, `Track`, and `MusicLibrary`.
   - Remove all `@Entity`, `@Column`, `@Id`, Jackson `@JsonProperty`, or Spring annotations from domain models.

2. **Define Driving and Driven Ports (`application/port`)**:
   - **Driving (Inbound) Ports**: Define interfaces for business operations (e.g., `ManageMusicLibraryUseCase`, `GetArtistQuery`).
   - **Driven (Outbound) Ports**: Define interfaces for data persistence (`ArtistPersistencePort`) and external service providers (`SpotifyApiPort`, `MusicBrainzApiPort`, `MusixmatchApiPort`).

3. **Implement Application Services (`application/service`)**:
   - Create orchestration services (`MusicLibraryApplicationService`, `ArtistApplicationService`) that accept domain objects and interact exclusively with Outbound Ports.

4. **Refactor Infrastructure to Adapters (`infrastructure/adapter`)**:
   - **Inbound REST Adapters**: Keep REST controllers (`ArtistController`) in `infrastructure.adapter.inbound.rest`. Map HTTP Request DTOs to domain objects/commands and call Inbound Ports.
   - **Outbound Persistence Adapters**: Create `ArtistPersistenceAdapter` in `infrastructure.adapter.outbound.persistence` that implements `ArtistPersistencePort`, delegates to Spring Data `ArtistRepository`, and converts between `ArtistEntity` and `Artist`.
   - **Outbound API Adapters**: Encapsulate API clients in `infrastructure.adapter.outbound.external`. Map API response DTOs into clean domain entities.

5. **Enforce Structural Constraints via ArchUnit**:
   - Implement ArchUnit rules in `archunittests` to enforce that `domain` has no outward dependencies, and `application` depends only on `domain` and outbound port interfaces.

---

## Utility & Cost-Benefit Analysis

- **Maximizing Value & Utility**:
  - **Isolating External Risk**: API breaking changes from Spotify or MusicBrainz are strictly contained inside outbound adapters, preventing cascade failures into business logic.
  - **Rapid Test Execution**: Pure domain entities and application services can be verified via ultra-fast unit tests without booting Spring contexts or Testcontainers, lowering CPU resource consumption and build times.
  - **Long-term Maintainability**: Reduces cognitive friction when adding new features or swapping database/messaging technologies.

- **Acceptable Trade-offs / Overhead**:
  - **Mapping Complexity**: Translating between REST DTOs, Domain Entities, JPA Entities, and External API DTOs introduces small mapping functions (which can be cleanly managed using Kotlin extension functions or Mappers).
  - **Initial Refactoring Effort**: Re-organizing package structures and creating port interfaces requires initial development effort, but pays off rapidly in reduced maintenance costs.
