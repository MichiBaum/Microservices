package com.michibaum.chess_service.apis.chesscom

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChesscomAccountDto(
    val avatar: String,
    @JsonProperty("player_id")
    val playerId: Long,
    @JsonProperty("@id")
    val id: String,
    val url: String,
    val name: String,
    val username: String,
    @JsonProperty("title", defaultValue = "")
    val title: String?,
    val country: String,
    @JsonProperty("last_online")
    val lastOnline: Long,
    val joined: Long,
    val status: String,
    val verified: Boolean,
)
