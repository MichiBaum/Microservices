package com.michibaum.music_service.database

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PlaylistSyncJobRepository : JpaRepository<PlaylistSyncJob, UUID> {
    fun findBySpotifyPlaylistId(spotifyPlaylistId: String): PlaylistSyncJob?
    fun findByPlaylistId(playlistId: UUID): List<PlaylistSyncJob>
    fun findByStatus(status: SyncJobStatus): List<PlaylistSyncJob>
    fun deleteByPlaylistId(playlistId: UUID)
}
