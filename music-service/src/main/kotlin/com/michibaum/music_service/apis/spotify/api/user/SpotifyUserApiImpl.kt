package com.michibaum.music_service.apis.spotify.api.user

import com.michibaum.music_service.apis.spotify.api.AbstractSpotifyApiClient
import com.michibaum.music_service.apis.spotify.api.user.dtos.TimeRange
import com.michibaum.music_service.apis.spotify.generated.model.GetFollowed200Response
import com.michibaum.music_service.apis.spotify.generated.model.GetUsersTopArtistsAndTracks200Response
import com.michibaum.music_service.apis.spotify.generated.model.PrivateUserObject
import com.michibaum.music_service.apis.spotify.generated.model.PublicUserObject
import com.michibaum.music_service.config.properties.ApisProperties
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class SpotifyUserApiImpl(
    restClientBuilder: RestClient.Builder,
    apisProperties: ApisProperties
): AbstractSpotifyApiClient(restClientBuilder, apisProperties), SpotifyUserApi {

    private val logger = LoggerFactory.getLogger(SpotifyUserApiImpl::class.java)

    override fun myProfile(credentials: SpotifyOAuthCredentials): PrivateUserObject? = tryCall {
        // https://developer.spotify.com/documentation/web-api/reference/get-current-users-profile
        client.get()
            .uri("/me")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .body(PrivateUserObject::class.java)
    }

    override fun myTopTracks(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): GetUsersTopArtistsAndTracks200Response? = tryCall {
        // https://developer.spotify.com/documentation/web-api/reference/get-users-top-artists-and-tracks
        client.get()
            .uri("/me/top/tracks?limit=50&time_range=${timeRange.spotify}")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .body(GetUsersTopArtistsAndTracks200Response::class.java)
    }

    override fun myTopArtists(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): GetUsersTopArtistsAndTracks200Response? = tryCall {
        // https://developer.spotify.com/documentation/web-api/reference/get-users-top-artists-and-tracks
        client.get()
            .uri("/me/top/artists?limit=50&time_range=${timeRange.spotify}")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .body(GetUsersTopArtistsAndTracks200Response::class.java)
    }

    override fun myFollowedArtists(credentials: SpotifyOAuthCredentials): GetFollowed200Response? = tryCall {
        // https://developer.spotify.com/documentation/web-api/reference/get-followed
        client.get()
            .uri("/me/following?type=artist&limit=50")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .body(GetFollowed200Response::class.java)
    }

    override fun profileFor(userId: String, credentials: SpotifyOAuthCredentials): PublicUserObject? = tryCall {
        client.get()
            .uri("/users/$userId")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .body(PublicUserObject::class.java)
    }

    private fun <T> tryCall(block: () -> T): T? =
        try {
            block()
        } catch (e: Exception) {
            logger.error("Spotify API call failed: ${e.message}", e)
            throw e
        }
}