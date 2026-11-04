package com.michibaum.music_service.database.spotify

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PlaylistSpotifyRepository : JpaRepository<PlaylistSpotify, UUID> {
    fun findBySpotifyId(spotifyId: String): PlaylistSpotify?
    fun deleteByPlaylistId(playlistId: UUID)
}
