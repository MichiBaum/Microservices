package com.michibaum.music_service.spotify.api

import com.michibaum.music_service.spotify.api.user.SpotifyUserApi
import com.michibaum.music_service.spotify.oauth.SpotifyOAuthCredentials
import org.springframework.stereotype.Component

@Component
class SpotifyApiImpl(
    private val spotifyUserApi: SpotifyUserApi
): SpotifyApi {

    override fun myProfile(credentials: SpotifyOAuthCredentials) {
        spotifyUserApi.myProfile(credentials)
    }

    override fun myTopTracks(credentials: SpotifyOAuthCredentials) {
        spotifyUserApi.myTopTracks(credentials)
    }

    override fun myTopArtists(credentials: SpotifyOAuthCredentials) {
        spotifyUserApi.myTopArtists(credentials)
    }

    override fun profileFor(userId: String, credentials: SpotifyOAuthCredentials) {
        spotifyUserApi.profileFor(userId, credentials)
    }
}