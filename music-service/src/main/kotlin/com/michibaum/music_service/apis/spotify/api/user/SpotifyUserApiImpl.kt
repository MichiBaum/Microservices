package com.michibaum.music_service.apis.spotify.api.user

import com.michibaum.music_service.apis.spotify.api.AbstractSpotifyApiClient
import com.michibaum.music_service.apis.spotify.api.user.dtos.TimeRange
import com.michibaum.music_service.apis.spotify.generated.model.GetFollowed200Response
import com.michibaum.music_service.apis.spotify.generated.model.GetUsersTopArtistsAndTracks200Response
import com.michibaum.music_service.apis.spotify.generated.model.PrivateUserObject
import com.michibaum.music_service.apis.spotify.generated.model.PublicUserObject
import com.michibaum.music_service.apis.spotify.oauth.SpotifyOAuthService
import com.michibaum.music_service.config.properties.ApisProperties
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient

@Component
class SpotifyUserApiImpl(
    restClientBuilder: RestClient.Builder,
    apisProperties: ApisProperties,
    private val spotifyOAuthService: SpotifyOAuthService,
): AbstractSpotifyApiClient(restClientBuilder, apisProperties), SpotifyUserApi {

    private val logger = LoggerFactory.getLogger(SpotifyUserApiImpl::class.java)

    override fun myProfile(credentials: SpotifyOAuthCredentials): PrivateUserObject? = tryCall(credentials) { creds ->
        // https://developer.spotify.com/documentation/web-api/reference/get-current-users-profile
        client.get()
            .uri("/me")
            .headers {
                it.setBearerAuth(creds.accessToken)
            }
            .retrieve()
            .body(PrivateUserObject::class.java)
    }

    override fun myTopTracks(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): GetUsersTopArtistsAndTracks200Response? = tryCall(credentials) { creds ->
        // https://developer.spotify.com/documentation/web-api/reference/get-users-top-artists-and-tracks
        client.get()
            .uri("/me/top/tracks?limit=50&time_range=${timeRange.spotify}")
            .headers {
                it.setBearerAuth(creds.accessToken)
            }
            .retrieve()
            .body(GetUsersTopArtistsAndTracks200Response::class.java)
    }

    override fun myTopArtists(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): GetUsersTopArtistsAndTracks200Response? = tryCall(credentials) { creds ->
        // https://developer.spotify.com/documentation/web-api/reference/get-users-top-artists-and-tracks
        client.get()
            .uri("/me/top/artists?limit=50&time_range=${timeRange.spotify}")
            .headers {
                it.setBearerAuth(creds.accessToken)
            }
            .retrieve()
            .body(GetUsersTopArtistsAndTracks200Response::class.java)
    }

    override fun myFollowedArtists(credentials: SpotifyOAuthCredentials, after: String?): GetFollowed200Response? = tryCall(credentials) { creds ->
        // https://developer.spotify.com/documentation/web-api/reference/get-followed
        val uri = if (!after.isNullOrBlank()) {
            "/me/following?type=artist&limit=50&after=$after"
        } else {
            "/me/following?type=artist&limit=50"
        }
        client.get()
            .uri(uri)
            .headers {
                it.setBearerAuth(creds.accessToken)
            }
            .retrieve()
            .body(GetFollowed200Response::class.java)
    }

    override fun profileFor(userId: String, credentials: SpotifyOAuthCredentials): PublicUserObject? = tryCall(credentials) { creds ->
        client.get()
            .uri("/users/$userId")
            .headers {
                it.setBearerAuth(creds.accessToken)
            }
            .retrieve()
            .body(PublicUserObject::class.java)
    }

    private fun <T> tryCall(credentials: SpotifyOAuthCredentials, block: (SpotifyOAuthCredentials) -> T): T? =
        try {
            block(credentials)
        } catch (e: HttpClientErrorException.Unauthorized) {
            logger.warn("Spotify API call returned 401 Unauthorized for user ${credentials.userId}. Retrying with forced token refresh...")
            val refreshedCredentials = spotifyOAuthService.getActiveToken(credentials.userId, force = true)
            if (refreshedCredentials != null) {
                try {
                    block(refreshedCredentials)
                } catch (retryException: Exception) {
                    logger.error("Spotify API call failed after forced token refresh for user ${credentials.userId}", retryException)
                    throw retryException
                }
            } else {
                logger.error("Failed to refresh Spotify token for user ${credentials.userId} after 401 Unauthorized")
                throw e
            }
        } catch (e: Exception) {
            logger.error("Spotify API call failed: ${e.message}", e)
            throw e
        }
}