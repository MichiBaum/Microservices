package com.michibaum.chess_service.app.game

import com.michibaum.chess_service.database.ChessPlatform
import com.michibaum.chess_service.database.GameType
import java.util.*

data class GameDto(
    val id: UUID,
    val chessPlatform: ChessPlatform,
    val platformId: String,
    val pgn: String,
    val gameType: GameType,
)
