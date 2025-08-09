package com.michibaum.music_service.apis.musicbrainz.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class ReleaseGroupResponse(
    val releases: List<Release>,
    @JsonProperty("secondary-type-ids")
    val secondaryTypeIds: List<Any?>,
    val disambiguation: String,
    @JsonProperty("primary-type")
    val primaryType: String,
    @JsonProperty("secondary-types")
    val secondaryTypes: List<Any?>,
    val id: String,
    @JsonProperty("artist-credit")
    val artistCredit: List<ArtistCredit2>,
    @JsonProperty("first-release-date")
    val firstReleaseDate: String,
    val title: String,
    @JsonProperty("primary-type-id")
    val primaryTypeId: String,
)

data class Release(
    val disambiguation: String,
    val title: String,
    val quality: String,
    @JsonProperty("text-representation")
    val textRepresentation: TextRepresentation,
    val packaging: String,
    val date: String,
    val id: String,
    @JsonProperty("status-id")
    val statusId: String,
    val barcode: String,
    @JsonProperty("release-events")
    val releaseEvents: List<Event>,
    val status: String,
    val country: String?,
    @JsonProperty("packaging-id")
    val packagingId: String,
    @JsonProperty("artist-credit")
    val artistCredit: List<ArtistCredit>,
)

data class TextRepresentation(
    val script: String,
    val language: String,
)

data class Event(
    val date: String,
    val area: Area?,
)



data class ArtistCredit(
    val artist: Artist,
    val name: String,
    val joinphrase: String,
)

data class Artist(
    val name: String,
    @JsonProperty("sort-name")
    val sortName: String,
    val id: String,
    @JsonProperty("type-id")
    val typeId: String,
    val country: String,
    val type: String,
    val disambiguation: String,
)

data class ArtistCredit2(
    val artist: Artist2,
    val name: String,
    val joinphrase: String,
)

data class Artist2(
    val id: String,
    @JsonProperty("sort-name")
    val sortName: String,
    @JsonProperty("type-id")
    val typeId: String,
    val country: String,
    val name: String,
    val type: String,
    val disambiguation: String,
)

