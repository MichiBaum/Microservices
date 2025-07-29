package com.michibaum.music_service.spotify.oauth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SpotifyOAuthCredentialsRepository: JpaRepository<SpotifyOAuthCredentials, UUID> {
    fun findByUserIdAndDeactivatedFalse(userId: String): SpotifyOAuthCredentials?
}