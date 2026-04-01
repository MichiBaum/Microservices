package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GameRepository: JpaRepository<Game, UUID> {
    fun existsByPlatformAndExternalGameId(platform: ChessPlatform, externalGameId: String): Boolean
    fun findByEvent(event: Event): List<Game>

    @Modifying
    @Query("update Game g set g.event = null where g.event = :event")
    fun nullifyEvent(event: Event)

}