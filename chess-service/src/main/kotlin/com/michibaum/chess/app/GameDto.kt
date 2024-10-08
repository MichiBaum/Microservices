package com.michibaum.chess.app

import com.michibaum.chess.domain.ChessPlatform
import com.michibaum.chess.domain.GameType
import java.util.*

data class GameDto(
    val id: UUID,
    val chessPlatform: ChessPlatform,
    val platformId: String,
    val pgn: String,
    val gameType: GameType,
)
