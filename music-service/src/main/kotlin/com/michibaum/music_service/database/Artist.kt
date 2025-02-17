package com.michibaum.music_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="artist")
data class Artist(

    @Column(nullable = false)
    val name: String,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
)
