package com.michibaum.music_service.database

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name="spotifyoauth_credentials")
class SpotifyOAuthCredentials(

    @Lob
    @Column(nullable = false, unique = true, length=512)
    val accessToken: String,

    @Column(nullable = false, unique = false)
    val expiresIn: Int,

    @Column(nullable = false, unique = true)
    val refreshToken: String,

    @Column(nullable = false, unique = false)
    val scope: String,

    @Column(nullable = false, unique = false)
    val userId: String,

    @Column(nullable = false, unique = false)
    val createdDate: Instant,

    @Column(nullable = false, unique = false)
    val validUntil: Instant,

    @Column(nullable = false, unique = false)
    var deactivated: Boolean = false,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)