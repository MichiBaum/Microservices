package com.michibaum.music_service.apis.spotify.api

import com.michibaum.music_service.apis.spotify.api.user.SpotifyUserApi
import com.michibaum.music_service.apis.spotify.api.user.dtos.TimeRange
import com.michibaum.music_service.apis.spotify.generated.model.GetFollowed200Response
import com.michibaum.music_service.apis.spotify.generated.model.GetUsersTopArtistsAndTracks200Response
import com.michibaum.music_service.apis.spotify.generated.model.PrivateUserObject
import com.michibaum.music_service.apis.spotify.generated.model.PublicUserObject
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import io.micrometer.observation.annotation.Observed
import org.springframework.stereotype.Component

@Component
class SpotifyApiImpl(
    private val spotifyUserApi: SpotifyUserApi,
): SpotifyApi {

    @Observed(name = "spotify.api.my.profile")
    override fun myProfile(credentials: SpotifyOAuthCredentials): PrivateUserObject? {
        return spotifyUserApi.myProfile(credentials)
    }

    @Observed(name = "spotify.api.my.top.tracks")
    override fun myTopTracks(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): GetUsersTopArtistsAndTracks200Response? {
        return spotifyUserApi.myTopTracks(timeRange, credentials)
    }

    @Observed(name = "spotify.api.my.top.artists")
    override fun myTopArtists(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): GetUsersTopArtistsAndTracks200Response? {
        return spotifyUserApi.myTopArtists(timeRange, credentials)
    }

    @Observed(name = "spotify.api.my.followed.artists")
    override fun myFollowedArtists(credentials: SpotifyOAuthCredentials, after: String?): GetFollowed200Response? {
        return spotifyUserApi.myFollowedArtists(credentials, after)
    }

    @Observed(name = "spotify.api.profile.for")
    override fun profileFor(userId: String, credentials: SpotifyOAuthCredentials): PublicUserObject? {
        return spotifyUserApi.profileFor(userId, credentials)
    }
}