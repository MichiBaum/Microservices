package com.michibaum.chess_service.app.opening

data class OpeningMoveDto(
    val id: String,
    val move: String,
    var nextMoves: List<OpeningMoveDto>
)
