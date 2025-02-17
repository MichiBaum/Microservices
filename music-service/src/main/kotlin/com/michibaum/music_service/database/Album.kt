package com.michibaum.music_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="album")
data class Album(

    @Column(nullable = false)
    val name: String,

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Artist::class)
    @JoinTable(name = "ALBUM_ARTIST_MAPPING", joinColumns = [JoinColumn(name = "album_id")], inverseJoinColumns = [JoinColumn(name = "artist_id")])
    val artists: List<Artist>,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
)
