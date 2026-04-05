package com.michibaum.chess_service.app.game

import com.michibaum.chess_service.database.*
import java.time.LocalDateTime
import java.util.*

data class GameRequestDto(
    val sourceType: SourceType,
    val platform: ChessPlatform,
    val externalGameId: String? = null,
    val pgn: String? = null,
    val openingName: String? = null,
    val gameResult: GameResult,
    val termination: TerminationType? = null,
    val playedAt: LocalDateTime,
    val timeControl: String? = null,
    val timeControlCategory: TimeControlCategory? = null,
    val variant: GameVariant = GameVariant.STANDARD,
    val whitePlayer: PlayerRequestDto,
    val blackPlayer: PlayerRequestDto,
    val eventId: UUID
)

data class PlayerRequestDto(
    val accountId: UUID,
    val username: String,
    val rating: Long? = null,
)
