package com.michibaum.music_service.apis.musixmatch.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class TrackResponse(
    val message: TrackResponseMessage,
)

data class TrackResponseMessage(
    val body: TrackResponseBody,
    val header: Header,
)

data class TrackResponseBody(
    val track: Track,
)

data class Track(
    @JsonProperty("album_coverart_100x100")
    val albumCoverart100x100: String,
    @JsonProperty("album_coverart_350x350")
    val albumCoverart350x350: String,
    @JsonProperty("album_coverart_500x500")
    val albumCoverart500x500: String,
    @JsonProperty("album_coverart_800x800")
    val albumCoverart800x800: String,
    @JsonProperty("album_id")
    val albumId: Long,
    @JsonProperty("album_name")
    val albumName: String,
    @JsonProperty("artist_id")
    val artistId: Long,
    @JsonProperty("artist_name")
    val artistName: String,
    @JsonProperty("commontrack_id")
    val commontrackId: Long,
    @JsonProperty("commontrack_isrcs")
    val commontrackIsrcs: List<List<String>>,
    val explicit: Long,
    @JsonProperty("has_lyrics")
    val hasLyrics: Long,
    @JsonProperty("has_richsync")
    val hasRichsync: Long,
    @JsonProperty("has_subtitles")
    val hasSubtitles: Long,
    val instrumental: Long,
    @JsonProperty("num_favourite")
    val numFavourite: Long,
    @JsonProperty("primary_genres")
    val primaryGenres: PrimaryGenres,
    val restricted: Long,
    @JsonProperty("track_edit_url")
    val trackEditUrl: String,
    @JsonProperty("track_id")
    val trackId: Long,
    @JsonProperty("track_isrc")
    val trackIsrc: String,
    @JsonProperty("track_length")
    val trackLength: Long,
    @JsonProperty("track_lyrics_translation_status")
    val trackLyricsTranslationStatus: List<Status>,
    @JsonProperty("track_name")
    val trackName: String,
    @JsonProperty("track_rating")
    val trackRating: Long,
    @JsonProperty("track_share_url")
    val trackShareUrl: String,
    @JsonProperty("track_spotify_id")
    val trackSpotifyId: String,
    @JsonProperty("updated_time")
    val updatedTime: String,
)

data class PrimaryGenres(
    @JsonProperty("music_genre_list")
    val musicGenreList: List<MusicGenreList>,
)

data class MusicGenreList(
    @JsonProperty("music_genre")
    val musicGenre: MusicGenre,
)

data class MusicGenre(
    @JsonProperty("music_genre_id")
    val musicGenreId: Long,
    @JsonProperty("music_genre_name")
    val musicGenreName: String,
    @JsonProperty("music_genre_name_extended")
    val musicGenreNameExtended: String,
    @JsonProperty("music_genre_parent_id")
    val musicGenreParentId: Long,
    @JsonProperty("music_genre_vanity")
    val musicGenreVanity: String,
)

data class Status(
    val from: String,
    val perc: Long,
    val to: String,
)