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
     * The International Standard Recording Code is an international standard code for uniquely identifying sound
     * recordings and music video recordings.
     *
     * https://community.metabrainz.org/t/same-recordings-with-multiple-isrc/473370/6
     */
    @ElementCollection(targetClass = String::class, fetch = FetchType.EAGER)
    @CollectionTable(name = "isrc", joinColumns = [JoinColumn(name = "track_id")])
    @Column(name = "isrc", nullable = false)
    val isrc: Set<String> = setOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
)