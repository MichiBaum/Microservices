package com.michibaum.music_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface PlaylistTrackRepository : JpaRepository<PlaylistTrack, UUID> {
    fun findByPlaylistId(playlistId: UUID): List<PlaylistTrack>
    
    fun findByPlaylistIdAndTrackId(playlistId: UUID, trackId: UUID): PlaylistTrack?
    
    fun deleteByPlaylistId(playlistId: UUID)
    
    fun deleteByPlaylistIdAndTrackId(playlistId: UUID, trackId: UUID)
    
    @Query("SELECT p FROM PlaylistTrack p WHERE p.playlist.id = :playlistId ORDER BY p.position ASC")
    fun findAllByPlaylistIdAndPositionAsc(playlistId: UUID): List<PlaylistTrack>
}
