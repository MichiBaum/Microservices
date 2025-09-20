package com.michibaum.music_service.database.spotify

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SpotifyAccountActivatedRepository: JpaRepository<SpotifyAccountActivated, UUID> {
    
    fun existsByUserId(userId: String): Boolean
    
}