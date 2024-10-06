package com.michibaum.chess.apis.lichess

data class LichessGameDto(
    val id: String,
    val rated: Boolean,
    val variant: String,
    val speed: String,
    val perf: String,
    val createdAt: Long,
    val lastMoveAt: Long,
    val status: String,
    val source: String,
    val players: Players,
    val winner: String?, // Can be null for some reason
    val moves: String,
    val pgn:String,
    val tournament: String?, // Can be null for some reason
    val clock: Clock? // Can be null for some reason
)

data class Players(
    val white: Player,
    val black: Player,
)

data class GamesUser(
    val name: String,
    val id: String,
)

/**
 * All fields can be null, because a bot is also a player
 */
data class Player(
    val user: GamesUser?,
    val rating: Long?,
    val provisional: Boolean?,
    val ratingDiff: Long?,
    val aiLevel: Int?
)

data class Clock(
    val initial: Long,
    val increment: Long,
    val totalTime: Long,
)