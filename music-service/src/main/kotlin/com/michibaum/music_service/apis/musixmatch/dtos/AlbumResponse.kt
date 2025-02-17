package com.michibaum.music_service.apis.musixmatch.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class AlbumResponse(
    val message: AlbumResponseMessage,
)

data class AlbumResponseMessage(
    val body: AlbumResponseBody,
    val header: Header,
)

data class AlbumResponseBody(
    val album: Album,
)

data class Album(
    @JsonProperty("album_id")
    val albumId: Long,
    @JsonProperty("album_name")
    val albumName: String,
    @JsonProperty("album_release_date")
    val albumReleaseDate: String,
    @JsonProperty("artist_id")
    val artistId: Long,
    @JsonProperty("artist_name")
    val artistName: String,
    val restricted: Long,
    @JsonProperty("updated_time")
    val updatedTime: String,
)