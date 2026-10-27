package com.michibaum.music_service.apis.spotify.api.user

import com.michibaum.music_service.apis.spotify.api.user.dtos.TimeRange
import com.michibaum.music_service.apis.spotify.generated.model.GetFollowed200Response
import com.michibaum.music_service.apis.spotify.generated.model.GetUsersTopArtistsAndTracks200Response
import com.michibaum.music_service.apis.spotify.generated.model.PrivateUserObject
import com.michibaum.music_service.apis.spotify.generated.model.PublicUserObject
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials

interface SpotifyUserApi {

    fun myProfile(credentials: SpotifyOAuthCredentials): PrivateUserObject?
    fun myTopTracks(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): GetUsersTopArtistsAndTracks200Response?
    fun myTopArtists(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): GetUsersTopArtistsAndTracks200Response?
    fun myFollowedArtists(credentials: SpotifyOAuthCredentials): GetFollowed200Response?

    fun profileFor(userId: String, credentials: SpotifyOAuthCredentials): PublicUserObject?
}