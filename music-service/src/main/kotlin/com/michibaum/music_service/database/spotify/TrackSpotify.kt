package com.michibaum.music_service.database.spotify

import com.michibaum.music_service.database.Track
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.UuidGenerator
import java.time.Instant
import java.util.*

@Entity
@Table(name="track_spotify")
data class TrackSpotify(

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="track_id", nullable=false)
    val track: Track,

    @Column(nullable = false, unique = true)
    val spotifyId: String,

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    val created: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated", nullable = false)
    val updated: Instant? = null,

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    val id: UUID? = null
)