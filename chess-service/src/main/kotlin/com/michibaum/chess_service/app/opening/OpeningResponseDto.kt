package com.michibaum.chess_service.app.opening


data class OpeningResponseDto(
    val id: String,
    val name: String,
    val moveId: String
)

data class PopularOpeningResponseDto (
    val id: String,
    val name: String,
    val moveId: String,
    val ranking: Int
)