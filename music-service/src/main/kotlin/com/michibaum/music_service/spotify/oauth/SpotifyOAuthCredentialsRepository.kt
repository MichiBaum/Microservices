package com.michibaum.music_service.spotify.oauth

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SpotifyOAuthCredentialsRepository: JpaRepository<SpotifyOAuthCredentials, UUID> {
    fun findByUserIdAndDeactivatedFalse(userId: String): SpotifyOAuthCredentials?
}