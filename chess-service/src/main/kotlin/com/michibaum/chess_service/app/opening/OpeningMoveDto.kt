package com.michibaum.chess_service.app.opening

data class SimpleOpeningMoveDto(
    val id: String,
    val move: String,
    val fen: String,
    val parentMoveId: String
)

data class OpeningMoveDto(
    val id: String,
    val move: String,
    val fen: String,
    var nextMoves: List<OpeningMoveDto>,
    var evaluations: List<EvaluationDto> = listOf(),
    val openingName: String?,
    val openingId: String?
)

data class EvaluationDto(
    val engineId: String,
    val engineName: String,
    val engineVersion: String,
    val depth: Int,
    val evaluation: String
)