package com.michibaum.music_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="spotifyoauth_data")
class SpotifyOAuthData(
    @Column(nullable = false, unique = true)
    val state: String,

    @Column(nullable = false, unique = false)
    val userId: String,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)