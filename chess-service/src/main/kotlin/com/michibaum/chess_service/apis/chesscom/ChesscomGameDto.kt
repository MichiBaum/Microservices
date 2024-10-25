package com.michibaum.chess_service.apis.chesscom

import com.fasterxml.jackson.annotation.JsonProperty

data class Gamesresult(
    val games: MutableList<ChesscomGameDto?> = mutableListOf()
)

data class ChesscomGameDto(
    val url: String?, // Can be null, don't know why
    val pgn: String?, // Can be null, don't know why
    @JsonProperty("time_control")
    val timeControl: String?, // Can be null, don't know why
    @JsonProperty("end_time")
    val endTime: Long,
    val rated: Boolean,
    val accuracies: Accuracies?, // Can be null, don't know why
    val tcn: String?, // Can be null, don't know why
    val uuid: String?, // Can be null, don't know why
    @JsonProperty("initial_setup")
    val initialSetup: String?, // Can be null, don't know why
    val fen: String?, // Can be null, don't know why
    @JsonProperty("time_class")
    val timeClass: String?, // Can be null, don't know why
    val rules: String?, // Can be null, don't know why
    val white: Player?, // Can be null, don't know why
    val black: Player?, // Can be null, don't know why
    val eco: String?, // Can be null, don't know why
)

data class Accuracies(
    val white: Double,
    val black: Double,
)

data class Player(
    val rating: Long,
    val result: String,
    @JsonProperty("@id")
    val id: String,
    val username: String,
    val uuid: String,
)
