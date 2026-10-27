package com.michibaum.music_service.database.spotify

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SpotifyOAuthCredentialsRepository: JpaRepository<SpotifyOAuthCredentials, UUID> {
    fun findByUserIdAndDeactivatedFalse(userId: String): SpotifyOAuthCredentials?
    fun findAllByUserIdAndDeactivatedFalse(userId: String): List<SpotifyOAuthCredentials>
    fun findByUserId(userId: String): List<SpotifyOAuthCredentials>
    fun findByRefreshToken(refreshToken: String): SpotifyOAuthCredentials?
}