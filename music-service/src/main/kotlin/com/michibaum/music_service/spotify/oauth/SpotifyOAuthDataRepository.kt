package com.michibaum.music_service.spotify.oauth

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SpotifyOAuthDataRepository: JpaRepository<SpotifyOAuthData, UUID> {
    fun findByState(state: String): SpotifyOAuthData?
}