package com.michibaum.music_service.apis.musixmatch.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class TopArtistsResponse(
    val message: TopArtistsResponseMessage,
)

data class TopArtistsResponseMessage(
    val body: TopArtistsResponseBody,
    val header: Header,
)

data class TopArtistsResponseBody(
    @JsonProperty("artist_list")
    val artistList: List<ArtistList>,
)

data class ArtistList(
    val artist: Artist,
)