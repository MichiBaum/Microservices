package com.michibaum.music_service.database

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Entity
@Table(name="album")
data class Album(

    @Column(nullable = false)
    val name: String,

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Artist::class)
    @JoinTable(name = "ALBUM_ARTIST_MAPPING", joinColumns = [JoinColumn(name = "album_id")], inverseJoinColumns = [JoinColumn(name = "artist_id")])
    val artists: MutableSet<Artist>,

    @Column(nullable = true)
    val releaseDate: LocalDate? = null,

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
