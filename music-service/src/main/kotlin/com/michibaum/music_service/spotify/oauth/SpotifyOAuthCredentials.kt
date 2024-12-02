package com.michibaum.music_service.spotify.oauth

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import org.hibernate.annotations.UuidGenerator
import java.time.Instant
import java.util.*

@Entity
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
    @UuidGenerator
    val id: UUID = UUID.randomUUID(),
)