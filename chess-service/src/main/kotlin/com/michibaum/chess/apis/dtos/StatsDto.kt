package com.michibaum.chess.apis.dtos

data class StatsDto(
    val rapid: Rating,
    val bullet: Rating,
    val blitz: Rating,
)

data class Rating(
    val highest: Double?,
    val lowest: Double?,
    val current: Double?
)
