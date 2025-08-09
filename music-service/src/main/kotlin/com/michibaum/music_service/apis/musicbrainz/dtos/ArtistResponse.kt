package com.michibaum.music_service.apis.musicbrainz.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ArtistResponse(
    @JsonProperty("sort-name")
    val sortName: String,
    val disambiguation: String,
    @JsonProperty("gender-id")
    val genderId: Any?,
    @JsonProperty("life-span")
    val lifeSpan: LifeSpan,
    val country: String,
    @JsonProperty("end-area")
    val endArea: Any?,
    @JsonProperty("begin-area")
    val beginArea: BeginArea,
    val isnis: List<String>,
    @JsonProperty("release-groups")
    val releaseGroups: List<Group>,
    val id: String,
    val name: String,
    val ipis: List<Any?>,
    val gender: Any?,
    @JsonProperty("type-id")
    val typeId: String,
    val type: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LifeSpan(
    val begin: String,
    val ended: Boolean,
    val end: Any?,
)

data class BeginArea(
    @JsonProperty("sort-name")
    val sortName: String,
    val disambiguation: String,
    @JsonProperty("iso-3166-2-codes")
    val iso31662Codes: List<String>,
    @JsonProperty("type-id")
    val typeId: Any?,
    val type: Any?,
    val id: String,
    val name: String,
)

data class Group(
    val disambiguation: String,
    val title: String,
    @JsonProperty("secondary-type-ids")
    val secondaryTypeIds: List<String>,
    @JsonProperty("secondary-types")
    val secondaryTypes: List<String>,
    @JsonProperty("primary-type-id")
    val primaryTypeId: String,
    @JsonProperty("first-release-date")
    val firstReleaseDate: String,
    val id: String,
    @JsonProperty("primary-type")
    val primaryType: String,
)

data class Area(
    @JsonProperty("sort-name")
    val sortName: String,
    val disambiguation: String,
    val id: String,
    val name: String,
    @JsonProperty("type-id")
    val typeId: Any?,
    @JsonProperty("iso-3166-1-codes")
    val iso31661Codes: List<String>,
    val type: Any?,
)

