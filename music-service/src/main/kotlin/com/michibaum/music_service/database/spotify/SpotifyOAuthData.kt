package com.michibaum.music_service.database.spotify

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
@Table(name="spotifyoauth_data")
class SpotifyOAuthData(
    @Column(nullable = false, unique = true)
    val state: String,

    @Column(nullable = false, unique = false)
    val userId: String,

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    val id: UUID? = null,
)