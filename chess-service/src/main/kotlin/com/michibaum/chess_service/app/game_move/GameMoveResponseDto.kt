package com.michibaum.chess_service.app.game_move

import java.util.UUID

data class GameMoveResponseDto(
    val id: UUID,
    val moveNumber: Int,
    val isWhite: Boolean,
    val move: String,
    val evaluations: List<GameMoveEvaluationResponseDto>
)

data class GameMoveEvaluationResponseDto(
    val id: UUID,
    val engineName: String,
    val engineVersion: String,
    val depth: Int,
    val evaluation: Float
)
