package com.michibaum.music_service.database

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Entity
@Table(name="track")
data class Track (

    @Column(nullable = false)
    val name: String,

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Artist::class)
    @JoinTable(name = "TRACK_ARTIST_MAPPING", joinColumns = [JoinColumn(name = "track_id")], inverseJoinColumns = [JoinColumn(name = "artist_id")])
    val artists: MutableSet<Artist>,

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    val releaseDate: LocalDate?,

    @ElementCollection(targetClass = String::class, fetch = FetchType.EAGER)
    @CollectionTable(name = "isrc", joinColumns = [JoinColumn(name = "track_id")])
    @Column(name = "isrc", nullable = false)
    val isrc: Set<String> = setOf(),

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