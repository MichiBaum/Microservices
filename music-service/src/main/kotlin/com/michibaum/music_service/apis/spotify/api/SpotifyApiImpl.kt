package com.michibaum.music_service.apis.spotify.api

import com.michibaum.music_service.apis.spotify.api.user.SpotifyUserApi
import com.michibaum.music_service.apis.spotify.api.user.dtos.SpotifyMeDto
import com.michibaum.music_service.apis.spotify.api.user.dtos.SpotifyTopItemsDto
import com.michibaum.music_service.apis.spotify.api.user.dtos.SpotifyUserDto
import com.michibaum.music_service.apis.spotify.api.user.dtos.TimeRange
import com.michibaum.music_service.database.SpotifyOAuthCredentials
import org.springframework.stereotype.Component

@Component
class SpotifyApiImpl(
    private val spotifyUserApi: SpotifyUserApi,
): SpotifyApi {

    override fun myProfile(credentials: SpotifyOAuthCredentials): SpotifyMeDto? {
        return spotifyUserApi.myProfile(credentials)
    }

    override fun myTopTracks(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): SpotifyTopItemsDto? {
        return spotifyUserApi.myTopTracks(timeRange, credentials)
    }

    override fun myTopArtists(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): SpotifyTopItemsDto? {
        return spotifyUserApi.myTopArtists(timeRange, credentials)
    }

    override fun profileFor(userId: String, credentials: SpotifyOAuthCredentials): SpotifyUserDto? {
        return spotifyUserApi.profileFor(userId, credentials)
    }
}