package com.michibaum.music_service.apis.musixmatch.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class AlbumTracksResponse(
    val message: AlbumTracksResponseMessage,
)

data class AlbumTracksResponseMessage(
    val body: AlbumTracksResponseBody,
    val header: Header,
)

data class AlbumTracksResponseBody(
    @JsonProperty("track_list")
    val trackList: List<TrackList>,
)