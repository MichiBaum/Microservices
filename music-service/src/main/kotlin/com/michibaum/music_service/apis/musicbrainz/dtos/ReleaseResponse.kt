package com.michibaum.music_service.apis.musicbrainz.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class ReleaseResponse(
    val id: String,
    @JsonProperty("text-representation")
    val textRepresentation: TextRepresentation,
    val quality: String,
    val packaging: String,
    val date: String,
    val title: String,
    val disambiguation: String,
    @JsonProperty("packaging-id")
    val packagingId: String,
    val country: String,
    val asin: Any?,
    @JsonProperty("release-events")
    val releaseEvents: List<Event>,
    val barcode: String,
    val status: String,
    @JsonProperty("status-id")
    val statusId: String,
    @JsonProperty("cover-art-archive")
    val coverArtArchive: CoverArtArchive,
)

data class TextRepresentation(
    val language: String,
    val script: String,
)

data class Event(
    val date: String,
    val area: Area,
)



data class CoverArtArchive(
    val artwork: Boolean,
    val back: Boolean,
    val count: Long,
    val darkened: Boolean,
    val front: Boolean,
)
