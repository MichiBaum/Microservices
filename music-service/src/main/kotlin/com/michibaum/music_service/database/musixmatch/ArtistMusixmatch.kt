package com.michibaum.music_service.database.musixmatch

import com.michibaum.music_service.database.Artist
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name="artist_musixmatch")
data class ArtistMusixmatch(

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="artist_id", nullable=false)
    val artist: Artist,

    @Column(nullable = false, unique = true)
    val musixmatchId: String,

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    val created: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated", nullable = false)
    val updated: Instant? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
)