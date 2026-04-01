package com.michibaum.chess_service.app.game

import com.michibaum.chess_service.database.*
import java.time.LocalDateTime
import java.util.*

data class GameResponseDto(
    val id: UUID,
    val sourceType: SourceType,
    val platform: ChessPlatform?,
    val externalGameId: String?,
    val pgn: String,
    val openingName: String?,
    val gameResult: GameResult,
    val termination: TerminationType?,
    val playedAt: LocalDateTime,
    val importedAt: LocalDateTime,
    val timeControl: String?,
    val timeControlCategory: TimeControlCategory?,
    val variant: GameVariant,
    val whitePlayer: PlayerResponseDto,
    val blackPlayer: PlayerResponseDto,
    val fen: String?,
    val round: String?,
    val boardNumber: Int?
)

data class PlayerResponseDto(
    val id: UUID,
    val username: String?,
    val rating: Long?,
    val pieceColor: PieceColor,
    val accountId: UUID?
)
