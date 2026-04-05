package com.michibaum.chess_service.app.opening

data class SimpleOpeningMoveResponseDto(
    val id: String,
    val move: String,
    val fen: String,
    val parentMoveId: String
)

data class OpeningMoveResponseDto(
    val id: String,
    val move: String,
    val fen: String,
    var nextMoves: List<OpeningMoveResponseDto>,
    var evaluations: List<EvaluationResponseDto> = listOf(),
    val openingName: String?,
    val openingId: String?
)

data class EvaluationResponseDto(
    val id: String,
    val engineId: String,
    val engineName: String,
    val engineVersion: String,
    val depth: Int,
    val evaluation: String
)

data class OpeningMoveEvaluationResponseDto(
    val id: String,
    val engineId: String,
    val depth: Int,
    val evaluation: String
)