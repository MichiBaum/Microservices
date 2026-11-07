package com.michibaum.music_service.database.spotify

import com.michibaum.music_service.database.Playlist
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "playlist_spotify")
class PlaylistSpotify(
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "playlist_id", nullable = false, unique = true)
    val playlist: Playlist,

    @Column(name = "spotify_id", nullable = false)
    val spotifyId: String,

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    val created: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated", nullable = false)
    var updated: Instant? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlaylistSpotify) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}
