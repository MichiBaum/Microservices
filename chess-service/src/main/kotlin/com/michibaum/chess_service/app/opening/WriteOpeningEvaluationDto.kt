package com.michibaum.chess_service.app.opening

data class WriteOpeningEvaluationDto(
    var engineId: String,
    var depth: Int,
    var evaluation: String
)