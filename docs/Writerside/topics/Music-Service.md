# Music Service

The Music Service is a microservice built with Kotlin and Spring Boot that provides functionality for managing and streaming music content. It handles music metadata, playlists, and playback functionality.

## Key Features

- **Music Library Management**: Manages a catalog of music tracks, albums, and artists.
- **Playlist Management**: Allows creation, modification, and sharing of playlists.
- **Music Metadata**: Stores and retrieves detailed information about music tracks.
- **Search Functionality**: Enables searching for music by various criteria (artist, album, genre, etc.).
- **Streaming Support**: Provides APIs for streaming music content.
- **User Preferences**: Tracks user listening history and preferences.
- **Recommendations**: Generates music recommendations based on user preferences and listening history.

## Music Data Model

The service manages several key entities:

- **Tracks**: Individual music tracks with metadata (title, duration, etc.)
- **Albums**: Collections of tracks with metadata (title, release date, cover art, etc.)
- **Artists**: Information about music artists and bands
- **Playlists**: User-created collections of tracks
- **Genres**: Categories for classifying music

## Integration with Other Services

- Integrates with the Authentication Service for user authentication
- Works with the Usermanagement Service for user preferences and history
- Communicates with storage services for music file management

## REST Endpoints

The Music Service exposes several REST endpoints for:

- Browsing and searching the music library
- Managing playlists
- Retrieving music metadata
- Streaming music content
- Managing user preferences

## Technologies

- Kotlin
- Spring Boot
- Spring Data JPA
- RESTful APIs
- Media streaming technologies
- Docker for containerization
