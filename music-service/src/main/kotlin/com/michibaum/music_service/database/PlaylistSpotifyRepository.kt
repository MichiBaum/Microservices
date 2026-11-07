package com.michibaum.music_service.database.spotify

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface PlaylistSpotifyRepository : JpaRepository<PlaylistSpotify, UUID> {
    fun findByPlaylistId(playlistId: UUID): PlaylistSpotify?
    fun existsByPlaylistId(playlistId: UUID): Boolean
    fun deleteByPlaylistId(playlistId: UUID)
    fun findByPlaylistIdIn(playlistIds: Collection<UUID>): List<PlaylistSpotify>

    @Query("SELECT ps FROM PlaylistSpotify ps JOIN ps.playlist p WHERE ps.spotifyId = :spotifyId AND p.createdBy = :userId")
    fun findBySpotifyIdAndUserId(spotifyId: String, userId: String): PlaylistSpotify?

    @Query("SELECT ps FROM PlaylistSpotify ps JOIN ps.playlist p WHERE ps.spotifyId IN :spotifyIds AND p.createdBy = :userId")
    fun findBySpotifyIdInAndUserId(spotifyIds: Collection<String>, userId: String): List<PlaylistSpotify>

    @Query("SELECT ps FROM PlaylistSpotify ps JOIN FETCH ps.playlist p WHERE p.createdBy = :userId")
    fun findAllByUserId(userId: String): List<PlaylistSpotify>
}
