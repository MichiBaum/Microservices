package com.michibaum.music_service.apis.spotify.api

import com.michibaum.music_service.apis.spotify.api.user.SpotifyUserApi
import com.michibaum.music_service.apis.spotify.api.user.dtos.TimeRange
import com.michibaum.music_service.apis.spotify.generated.model.GetFollowed200Response
import com.michibaum.music_service.apis.spotify.generated.model.GetUsersTopArtistsAndTracks200Response
import com.michibaum.music_service.apis.spotify.generated.model.PrivateUserObject
import com.michibaum.music_service.apis.spotify.generated.model.PublicUserObject
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import org.springframework.stereotype.Component

@Component
class SpotifyApiImpl(
    private val spotifyUserApi: SpotifyUserApi,
): SpotifyApi {

    override fun myProfile(credentials: SpotifyOAuthCredentials): PrivateUserObject? {
        return spotifyUserApi.myProfile(credentials)
    }

    override fun myTopTracks(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): GetUsersTopArtistsAndTracks200Response? {
        return spotifyUserApi.myTopTracks(timeRange, credentials)
    }

    override fun myTopArtists(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): GetUsersTopArtistsAndTracks200Response? {
        return spotifyUserApi.myTopArtists(timeRange, credentials)
    }

    override fun myFollowedArtists(credentials: SpotifyOAuthCredentials): GetFollowed200Response? {
        return spotifyUserApi.myFollowedArtists(credentials)
    }

    override fun profileFor(userId: String, credentials: SpotifyOAuthCredentials): PublicUserObject? {
        return spotifyUserApi.profileFor(userId, credentials)
    }
}