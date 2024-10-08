package com.michibaum.chess.app

import com.michibaum.chess.domain.ChessPlatform
import com.michibaum.chess.domain.Game
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GameRepository: JpaRepository<Game, UUID> {
    fun existsByChessPlatformAndPlatformId(chessPlatform: ChessPlatform, id: String): Boolean

}