package com.michibaum.music_service.apis.spotify.api.user

import com.michibaum.music_service.apis.spotify.api.user.dtos.SpotifyMeDto
import com.michibaum.music_service.apis.spotify.api.user.dtos.SpotifyTopItemsDto
import com.michibaum.music_service.apis.spotify.api.user.dtos.SpotifyUserDto
import com.michibaum.music_service.apis.spotify.api.user.dtos.TimeRange
import com.michibaum.music_service.database.SpotifyOAuthCredentials

interface SpotifyUserApi {

    fun myProfile(credentials: SpotifyOAuthCredentials): SpotifyMeDto?
    fun myTopTracks(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): SpotifyTopItemsDto?
    fun myTopArtists(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): SpotifyTopItemsDto?

    fun profileFor(userId: String, credentials: SpotifyOAuthCredentials): SpotifyUserDto?
}