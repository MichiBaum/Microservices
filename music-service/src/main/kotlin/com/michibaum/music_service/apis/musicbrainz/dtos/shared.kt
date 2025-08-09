package com.michibaum.music_service.apis.musicbrainz.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Area(
    val disambiguation: String,
    val name: String,
    @JsonProperty("iso-3166-1-codes")
    val iso31661Codes: List<String>,
    val type: Any?,
    @JsonProperty("type-id")
    val typeId: Any?,
    @JsonProperty("sort-name")
    val sortName: String,
    val id: String,
)