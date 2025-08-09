package com.michibaum.music_service.database.musixmatch

import com.michibaum.music_service.database.Track
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name="track_musixmatch")
data class TrackMusixmatch(

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="track_id", nullable=false)
    val track: Track,

    @Column(nullable = false, unique = true)
    val musixmatchId: String,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    val created: Instant? = null,

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated", nullable = false)
    val updated: Instant? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
)