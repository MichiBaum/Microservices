package com.michibaum.chess_service.apis.chesscom

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChesscomStatsDto(
    @JsonProperty("chess_rapid")
    val chessRapid: Rapid?,
    @JsonProperty("chess_bullet")
    val chessBullet: Bullet?,
    @JsonProperty("chess_blitz")
    val chessBlitz: Blitz?,
    val fide: Double?,
)

data class Rapid(
    val last: Last,
    val best: Best,
    val record: Record,
)

data class Last(
    val rating: Double,
    val date: Long,
    val rd: Long,
)

data class Best(
    val rating: Double,
    val date: Long,
    val game: String,
)

data class Record(
    val win: Long,
    val loss: Long,
    val draw: Long,
)

data class Bullet(
    val last: Last,
    val best: Best,
    val record: Record,
)

data class Blitz(
    val last: Last,
    val best: Best,
    val record: Record,
)

data class Highest(
    val rating: Double,
    val date: Long,
)

data class Lowest(
    val rating: Double,
    val date: Long,
)