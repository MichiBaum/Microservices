package com.michibaum.music_service.database.spotify

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SpotifyAccountActivatedRepository: JpaRepository<SpotifyAccountActivated, UUID> {
    
    @Query("SELECT s FROM SpotifyAccountActivated s WHERE s.userId = :userId")
    fun existsByUserId(@Param("userId") userId: String): List<SpotifyAccountActivated>
    
}