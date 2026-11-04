-- Migration: V6__add_playlist_entities.sql
-- Description: Add playlist entities for Spotify playlist import feature
-- Date: 2026-11-04

-- Table: playlist
-- Stores internal playlists (all private, no public toggle)
CREATE TABLE playlist (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    cover_image_url VARCHAR(1000),
    created_by VARCHAR(255) NOT NULL,
    created DATETIME(6) NOT NULL,
    updated DATETIME(6) NOT NULL
);

-- Table: playlist_track
-- Join table linking tracks to playlists with position tracking
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

-- Table: playlist_spotify
-- Maps internal playlists to Spotify playlists (similar to artist_spotify pattern)
CREATE TABLE playlist_spotify (
    id UUID NOT NULL PRIMARY KEY,
    playlist_id UUID NOT NULL,
    spotify_id VARCHAR(255) NOT NULL,
    created DATETIME(6) NOT NULL,
    updated DATETIME(6) NOT NULL,
    CONSTRAINT uk_spotify_id UNIQUE (spotify_id),
    FOREIGN KEY (playlist_id) REFERENCES playlist(id)
);

-- Table: playlist_sync_job
-- Tracks async sync operations from Spotify
CREATE TABLE playlist_sync_job (
    id UUID NOT NULL PRIMARY KEY,
    playlist_id UUID NOT NULL,
    spotify_playlist_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    started_at DATETIME(6) NOT NULL,
    completed_at DATETIME(6),
    track_count INT,
    error_message TEXT,
    created DATETIME(6) NOT NULL,
    CONSTRAINT uk_sync_job_spotify UNIQUE (spotify_playlist_id),
    FOREIGN KEY (playlist_id) REFERENCES playlist(id)
);
