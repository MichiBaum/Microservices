package com.michibaum.music_service.database.spotify

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name="spotifyoauth_credentials")
class SpotifyOAuthCredentials(

    @Lob
    @Column(nullable = false, unique = true, length=512)
    var accessToken: String,

    @Column(nullable = false, unique = false)
    var expiresIn: Int,

    @Column(nullable = false, unique = true)
    var refreshToken: String,

    @Column(nullable = false, unique = false, length = 2048)
    var scope: String,

    @Column(nullable = false, unique = false)
    val userId: String,

    @Column(nullable = false, unique = false)
    var createdDate: Instant,

    @Column(nullable = false, unique = false)
    var validUntil: Instant,

    @Column(nullable = false, unique = false)
    var deactivated: Boolean = false,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)