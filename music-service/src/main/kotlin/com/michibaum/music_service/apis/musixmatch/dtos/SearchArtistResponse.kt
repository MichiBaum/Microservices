package com.michibaum.music_service.apis.musixmatch.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class SearchArtistResponse(
    val message: SearchArtistResponseMessage,
)

data class SearchArtistResponseMessage(
    val body: SearchArtistResponseBody,
    val header: Header,
)

data class SearchArtistResponseBody(
    @JsonProperty("artist_list")
    val artistList: List<ArtistList>,
)