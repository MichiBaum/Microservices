package com.michibaum.chess_service.app

import com.michibaum.chess_service.domain.ChessPlatform
import com.michibaum.chess_service.domain.Game
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GameRepository: JpaRepository<Game, UUID> {
    fun existsByChessPlatformAndPlatformId(chessPlatform: ChessPlatform, id: String): Boolean

}