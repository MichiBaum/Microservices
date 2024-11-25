package com.michibaum.chess_service.app.event

data class ParticipantDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val fideId: String?
)
