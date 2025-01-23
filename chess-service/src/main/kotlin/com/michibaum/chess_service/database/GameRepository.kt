package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface GameRepository: JpaRepository<Game, UUID> {
    fun existsByChessPlatformAndPlatformId(chessPlatform: ChessPlatform, id: String): Boolean
    fun findByEvent(event: Event): List<Game>

}