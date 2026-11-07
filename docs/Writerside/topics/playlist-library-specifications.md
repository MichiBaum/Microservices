# Playlist Library Specifications

This document outlines the architectural design, domain model, and REST API contract for the Playlist feature within `music-service`.

## Overview

The Playlist feature supports two types of playlists:
1. **Internal Playlists**: User-created playlists where tracks can be added, reordered, or removed.
2. **Spotify-Linked Playlists**: Read-only playlists synchronized directly from Spotify into the user's library. These mirror the upstream Spotify playlist's metadata and track list.

## Core Architecture & Guidelines

### 1. Read-Only Mutation Guards
Spotify-linked playlists are managed upstream on Spotify.
- Any attempt to add tracks (`POST /api/playlists/{id}/tracks`), remove tracks (`DELETE /api/playlists/{id}/tracks/{trackId}`), or edit metadata (`PUT /api/playlists/{id}`) on a Spotify-linked playlist is rejected with HTTP `400 Bad Request`.
- Playlist deletion (`DELETE /api/playlists/{id}`) is permitted for both internal and Spotify-linked playlists, allowing users to remove an imported playlist from their local library without affecting Spotify.

### 2. Multi-Tenancy & In-Place Re-Syncing
- Every `Playlist` entity is owned by the user who created or synced it (`createdBy = userId`).
- The `playlist_spotify` table enforces a unique constraint on `playlist_id` (one internal playlist can map to at most one Spotify playlist), but `spotify_id` is indexed and not globally unique.
- Multiple users can independently sync or follow the same Spotify playlist without constraint violations.
- When a user initiates a re-sync, the service queries existing links by `(spotify_id, userId)`. If found, the existing playlist is updated in-place rather than duplicated.

### 3. Track & Artist Ingestion
When syncing a Spotify playlist:
- For every track, the service checks `track_spotify` by Spotify ID. If present, the existing local `Track` is reused.
- If missing, missing `Artist` and `ArtistSpotify` records are cataloged, and a new `Track` along with its `TrackSpotify` mapping is persisted.
- No tracks are dropped or marked duplicate during sync; playlists preserve 100% of their playable tracks.

### 4. Re-Sync Mirroring Strategy
During re-sync:
- The playlist's `name`, `description`, `coverImageUrl`, and `updated` timestamp are refreshed.
- Existing `playlist_track` entries for that playlist are cleared and replaced with the latest ordered Spotify tracklist.

### 5. Full Pagination
- `SpotifyPlaylistApiImpl.getPlaylistTracks` implements cursor/offset pagination (querying `/playlists/{playlistId}/items?limit=50&offset={offset}`) until Spotify returns `next == null` or all tracks are retrieved. Playlists exceeding 100 tracks are imported completely.

### 6. Synchronous Direct Execution
- Synchronization executes directly in the request lifecycle (matching `MusicLibraryService.syncLibraryArtists`) and returns the resulting playlists immediately without transient background job entities.

---

## Database Schema (MariaDB & Flyway V6)

```sql
-- Table: playlist
CREATE TABLE playlist (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    cover_image_url VARCHAR(1000),
    created_by VARCHAR(255) NOT NULL,
    created DATETIME(6) NOT NULL,
    updated DATETIME(6) NOT NULL
);
CREATE INDEX idx_playlist_created_by ON playlist (created_by);

-- Table: playlist_track
CREATE TABLE playlist_track (
    id UUID NOT NULL PRIMARY KEY,
    playlist_id UUID NOT NULL,
    track_id UUID NOT NULL,
    position INT NOT NULL DEFAULT 0,
    created DATETIME(6) NOT NULL,
    FOREIGN KEY (playlist_id) REFERENCES playlist(id),
    FOREIGN KEY (track_id) REFERENCES track(id),
    CONSTRAINT uk_playlist_track UNIQUE (playlist_id, track_id)
);
CREATE INDEX idx_playlist_track_playlist ON playlist_track (playlist_id);

-- Table: playlist_spotify
CREATE TABLE playlist_spotify (
    id UUID NOT NULL PRIMARY KEY,
    playlist_id UUID NOT NULL,
    spotify_id VARCHAR(255) NOT NULL,
    created DATETIME(6) NOT NULL,
    updated DATETIME(6) NOT NULL,
    CONSTRAINT uk_playlist_spotify_playlist UNIQUE (playlist_id),
    FOREIGN KEY (playlist_id) REFERENCES playlist(id)
);
CREATE INDEX idx_playlist_spotify_spotify_id ON playlist_spotify (spotify_id);
```

---

## REST Endpoints (`/api/playlists` and `/api/music/library/playlists`)

| Method | Endpoint | Description | Guard / Behavior |
|--------|----------|-------------|------------------|
| `GET` | `/api/playlists` | List all playlists for authenticated user | Returns `List<PlaylistDto>` with `isSpotify` flag and `trackCount`. |
| `POST` | `/api/playlists` | Create a new internal playlist | Creates internal playlist owned by user. |
| `GET` | `/api/playlists/{id}` | Get playlist details and tracks | Returns `PlaylistDetailsDto` with ordered track list. |
| `PUT` | `/api/playlists/{id}` | Update playlist name/description/image | Rejects Spotify-linked playlists with `400 Bad Request`. |
| `DELETE` | `/api/playlists/{id}` | Delete playlist | Deletes playlist and child associations. Allowed for all playlists. |
| `POST` | `/api/playlists/{id}/tracks` | Add a track to playlist | Rejects Spotify-linked playlists with `400 Bad Request`. |
| `DELETE` | `/api/playlists/{id}/tracks/{trackId}` | Remove a track from playlist | Rejects Spotify-linked playlists with `400 Bad Request`. |
| `POST` | `/api/playlists/sync` | Sync user playlists from Spotify | Syncs all or specified Spotify playlists directly. |
| `POST` | `/api/playlists/{id}/sync` | Re-sync a single Spotify playlist | Re-syncs metadata and mirrors latest tracks. |
