package com.michibaum.music_service.database

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name="track")
data class Track (

    @Column(nullable = false)
    val name: String,

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Artist::class)
    @JoinTable(name = "TRACK_ARTIST_MAPPING", joinColumns = [JoinColumn(name = "track_id")], inverseJoinColumns = [JoinColumn(name = "artist_id")])
    val artists: List<Artist>,

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    val releaseDate: LocalDate?,

    /**
     * International Standard Recording Code
     */
    @Column(nullable = false, unique = true)
    val isrc: String,

    /**
     * International Article Number
     */
    @Column(nullable = true)
    val ean: String?,

    /**
     * Universal Product Code
     */
    @Column(nullable = true)
    val upc: String?,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
)