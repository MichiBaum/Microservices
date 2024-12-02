package com.michibaum.chess_service.app.event

data class ParticipantDto(
    val id: String,
    val firstname: String,
    val lastname: String,
    val fideId: String?
)
