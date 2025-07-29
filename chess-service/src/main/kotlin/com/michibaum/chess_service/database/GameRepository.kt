package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GameRepository: JpaRepository<Game, UUID> {
    fun existsByChessPlatformAndPlatformId(chessPlatform: ChessPlatform, id: String): Boolean
    fun findByEvent(event: Event): List<Game>

}