package com.michibaum.music_service.apis.musixmatch.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class TopTracksResponse(
    val message: TopTracksResponseMessage,
)

data class TopTracksResponseMessage(
    val body: TopTracksResponseBody,
    val header: Header,
)

data class TopTracksResponseBody(
    @JsonProperty("track_list")
    val trackList: List<TrackList>,
)

data class TrackList(
    val track: Track,
)