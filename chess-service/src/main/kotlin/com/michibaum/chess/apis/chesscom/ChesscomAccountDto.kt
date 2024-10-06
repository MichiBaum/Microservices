package com.michibaum.chess.apis.chesscom

import com.fasterxml.jackson.annotation.JsonProperty

data class ChesscomAccountDto(
    val avatar: String,
    @JsonProperty("player_id")
    val playerId: Long,
    @JsonProperty("@id")
    val id: String,
    val url: String,
    val name: String,
    val username: String,
    val followers: Long,
    val country: String,
    @JsonProperty("last_online")
    val lastOnline: Long,
    val joined: Long,
    val status: String,
    @JsonProperty("is_streamer")
    val isStreamer: Boolean,
    val verified: Boolean,
    val league: String,
    @JsonProperty("streaming_platforms")
    val streamingPlatforms: List<Any?>
)
