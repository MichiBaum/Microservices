package com.michibaum.music_service.apis.musixmatch.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class ArtistResponse(
    val message: ArtistResponseMessage,
)

data class ArtistResponseMessage(
    val body: ArtistResponseBody,
    val header: Header,
)

data class ArtistResponseBody(
    val artist: Artist,
)

data class Artist(
    @JsonProperty("artist_alias_list")
    val artistAliasList: List<ArtistAliasList>,
    @JsonProperty("artist_comment")
    val artistComment: String,
    @JsonProperty("artist_country")
    val artistCountry: String,
    @JsonProperty("artist_credits")
    val artistCredits: ArtistCredits,
    @JsonProperty("artist_id")
    val artistId: Long,
    @JsonProperty("artist_name")
    val artistName: String,
    @JsonProperty("artist_name_translation_list")
    val artistNameTranslationList: List<ArtistNameTranslationList>,
    @JsonProperty("artist_twitter_url")
    val artistTwitterUrl: String,
    @JsonProperty("begin_date")
    val beginDate: String,
    @JsonProperty("begin_date_year")
    val beginDateYear: String,
    @JsonProperty("end_date")
    val endDate: String,
    @JsonProperty("end_date_year")
    val endDateYear: String,
    val restricted: Long,
    @JsonProperty("updated_time")
    val updatedTime: String,
)

data class ArtistAliasList(
    @JsonProperty("artist_alias")
    val artistAlias: String,
)

data class ArtistCredits(
    @JsonProperty("artist_list")
    val artistList: List<Any?>,
)

data class ArtistNameTranslationList(
    @JsonProperty("artist_name_translation")
    val artistNameTranslation: ArtistNameTranslation,
)

data class ArtistNameTranslation(
    val language: String,
    val translation: String,
)