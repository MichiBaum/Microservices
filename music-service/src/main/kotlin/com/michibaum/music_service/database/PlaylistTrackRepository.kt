package com.michibaum.music_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface PlaylistTrackRepository : JpaRepository<PlaylistTrack, UUID> {
    fun findByPlaylistId(playlistId: UUID): List<PlaylistTrack>
    
    fun findByPlaylistIdAndTrackId(playlistId: UUID, trackId: UUID): PlaylistTrack?
    
    fun deleteByPlaylistId(playlistId: UUID)
    
    fun deleteByPlaylistIdAndTrackId(playlistId: UUID, trackId: UUID)
    
    @Query("SELECT pt FROM PlaylistTrack pt JOIN FETCH pt.track t LEFT JOIN FETCH t.artists WHERE pt.playlist.id = :playlistId ORDER BY pt.position ASC")
    fun findAllByPlaylistIdWithTracks(playlistId: UUID): List<PlaylistTrack>

    @Query("SELECT p FROM PlaylistTrack p WHERE p.playlist.id = :playlistId ORDER BY p.position ASC")
    fun findAllByPlaylistIdAndPositionAsc(playlistId: UUID): List<PlaylistTrack>

    fun countByPlaylistId(playlistId: UUID): Int

    @Query("SELECT pt.playlist.id, count(pt) FROM PlaylistTrack pt WHERE pt.playlist.id IN :playlistIds GROUP BY pt.playlist.id")
    fun countByPlaylistIds(playlistIds: Collection<UUID>): List<Array<Any>>
}
