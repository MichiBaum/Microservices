package com.michibaum.music_service.database

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "playlist_sync_job")
data class PlaylistSyncJob(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "playlist_id", nullable = false)
    val playlist: Playlist,

    @Column(name = "spotify_playlist_id", nullable = false)
    val spotifyPlaylistId: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SyncJobStatus,

    @Column(name = "started_at", nullable = false)
    var startedAt: Instant? = null,

    @Column(name = "completed_at")
    var completedAt: Instant? = null,

    @Column(name = "track_count")
    var trackCount: Int? = null,

    @Column(name = "error_message")
    var errorMessage: String? = null,

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    val created: Instant? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
)

enum class SyncJobStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED
}
