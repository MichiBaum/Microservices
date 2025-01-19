package com.michibaum.chess_service.app.opening

import java.util.UUID

data class OpeningDto(
    val name: String,
    val moveId: UUID
)
