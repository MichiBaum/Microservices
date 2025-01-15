package com.michibaum.chess_service.app.opening

data class OpeningMoveDto(
    val id: String,
    val move: String,
    var nextMoves: List<OpeningMoveDto>,
    var evaluations: List<EvaluationDto> = listOf()
)

data class EvaluationDto(
    val engineId: String,
    val depth: Int,
    val evaluation: String
)