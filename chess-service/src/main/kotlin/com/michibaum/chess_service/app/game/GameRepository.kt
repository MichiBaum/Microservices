package com.michibaum.chess_service.app.game

import com.michibaum.chess_service.domain.ChessPlatform
import com.michibaum.chess_service.domain.Event
import com.michibaum.chess_service.domain.Game
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface GameRepository: JpaRepository<Game, UUID> {
    fun existsByChessPlatformAndPlatformId(chessPlatform: ChessPlatform, id: String): Boolean
    fun findByEvent(event: Event): List<Game>

}