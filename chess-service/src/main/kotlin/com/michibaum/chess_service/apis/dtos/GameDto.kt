package com.michibaum.chess_service.apis.dtos

import com.michibaum.chess_service.database.*
import java.time.LocalDateTime


data class GameDto(
    val platform: ChessPlatform,
    val id: String,
    val players: List<PlayerDto>,
    val pgn: String,
    val timeControlCategory: TimeControlCategory,
    val gameResult: GameResult,
    val playedAt: LocalDateTime
)

data class PlayerDto(
    val id: String,
    val username: String,
    val rating: Long?,
    val pieceColor: PieceColor
)

