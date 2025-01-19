package com.michibaum.chess_service.app.opening

data class WriteOpeningMoveDto(
    val id: String,
    val move: String,
    val parentMoveId: String
)
