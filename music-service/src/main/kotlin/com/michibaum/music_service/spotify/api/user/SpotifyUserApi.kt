package com.michibaum.music_service.spotify.api.user

import com.michibaum.music_service.spotify.oauth.SpotifyOAuthCredentials

interface SpotifyUserApi {

    fun myProfile(credentials: SpotifyOAuthCredentials)
    fun myTopTracks(credentials: SpotifyOAuthCredentials)
    fun myTopArtists(credentials: SpotifyOAuthCredentials)

    fun profileFor(userId: String, credentials: SpotifyOAuthCredentials)
}