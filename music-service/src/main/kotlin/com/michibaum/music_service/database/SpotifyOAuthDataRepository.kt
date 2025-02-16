package com.michibaum.music_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SpotifyOAuthDataRepository: JpaRepository<SpotifyOAuthData, UUID> {
    fun findByState(state: String): SpotifyOAuthData?
}