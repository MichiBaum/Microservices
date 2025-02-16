package com.michibaum.music_service.database

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SpotifyOAuthDataRepository: JpaRepository<SpotifyOAuthData, UUID> {
    fun findByState(state: String): SpotifyOAuthData?
}