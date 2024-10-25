package com.michibaum.chess_service.apis.chesscom

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChesscomLeaderboards(
    @JsonProperty("live_rapid")
    val liveRapid: List<LiveRapid>,
    @JsonProperty("live_blitz")
    val liveBlitz: List<LiveBlitz>,
    @JsonProperty("live_bullet")
    val liveBullet: List<LiveBullet>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LiveRapid(
    @JsonProperty("player_id")
    val playerId: Long,
    @JsonProperty("@id")
    val id: String,
    val url: String,
    val username: String,
    val score: Long,
    val rank: Long,
    val country: String,
    val title: String?,
    val name: String?,
    val status: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LiveBlitz(
    @JsonProperty("player_id")
    val playerId: Long,
    @JsonProperty("@id")
    val id: String,
    val url: String,
    val username: String,
    val score: Long,
    val rank: Long,
    val country: String,
    val title: String,
    val name: String,
    val status: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LiveBullet(
    @JsonProperty("player_id")
    val playerId: Long,
    @JsonProperty("@id")
    val id: String,
    val url: String,
    val username: String,
    val score: Long,
    val rank: Long,
    val country: String,
    val name: String,
    val status: String,
)