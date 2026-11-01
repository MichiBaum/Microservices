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
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.annotation.Observed
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient

@Component
class SpotifyUserApiImpl(
    restClientBuilder: RestClient.Builder,
    apisProperties: ApisProperties,
    private val spotifyOAuthService: SpotifyOAuthService,
    private val observationRegistry: ObservationRegistry? = null,
): AbstractSpotifyApiClient(restClientBuilder, apisProperties), SpotifyUserApi {

    private val logger = LoggerFactory.getLogger(SpotifyUserApiImpl::class.java)

    @Observed(name = "spotify.api.my.profile")
    override fun myProfile(credentials: SpotifyOAuthCredentials): PrivateUserObject? = tryCall("myProfile", credentials) { creds ->
        // https://developer.spotify.com/documentation/web-api/reference/get-current-users-profile
        client.get()
            .uri("/me")
            .headers {
                it.setBearerAuth(creds.accessToken)
            }
            .retrieve()
            .body(PrivateUserObject::class.java)
    }

    @Observed(name = "spotify.api.my.top.tracks")
    override fun myTopTracks(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): GetUsersTopArtistsAndTracks200Response? = tryCall("myTopTracks", credentials) { creds ->
        // https://developer.spotify.com/documentation/web-api/reference/get-users-top-artists-and-tracks
        client.get()
            .uri("/me/top/tracks?limit=50&time_range={timeRange}", timeRange.spotify)
            .headers {
                it.setBearerAuth(creds.accessToken)
            }
            .retrieve()
            .body(GetUsersTopArtistsAndTracks200Response::class.java)
    }

    @Observed(name = "spotify.api.my.top.artists")
    override fun myTopArtists(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): GetUsersTopArtistsAndTracks200Response? = tryCall("myTopArtists", credentials) { creds ->
        // https://developer.spotify.com/documentation/web-api/reference/get-users-top-artists-and-tracks
        client.get()
            .uri("/me/top/artists?limit=50&time_range={timeRange}", timeRange.spotify)
            .headers {
                it.setBearerAuth(creds.accessToken)
            }
            .retrieve()
            .body(GetUsersTopArtistsAndTracks200Response::class.java)
    }

    @Observed(name = "spotify.api.my.followed.artists")
    override fun myFollowedArtists(credentials: SpotifyOAuthCredentials, after: String?): GetFollowed200Response? = tryCall("myFollowedArtists", credentials) { creds ->
        // https://developer.spotify.com/documentation/web-api/reference/get-followed
        if (!after.isNullOrBlank()) {
            client.get()
                .uri("/me/following?type=artist&limit=50&after={after}", after)
                .headers {
                    it.setBearerAuth(creds.accessToken)
                }
                .retrieve()
                .body(GetFollowed200Response::class.java)
        } else {
            client.get()
                .uri("/me/following?type=artist&limit=50")
                .headers {
                    it.setBearerAuth(creds.accessToken)
                }
                .retrieve()
                .body(GetFollowed200Response::class.java)
        }
    }

    @Observed(name = "spotify.api.profile.for")
    override fun profileFor(userId: String, credentials: SpotifyOAuthCredentials): PublicUserObject? = tryCall("profileFor", credentials) { creds ->
        client.get()
            .uri("/users/{userId}", userId)
            .headers {
                it.setBearerAuth(creds.accessToken)
            }
            .retrieve()
            .body(PublicUserObject::class.java)
    }

    private fun <T> tryCall(methodName: String, credentials: SpotifyOAuthCredentials, block: (SpotifyOAuthCredentials) -> T): T? {
        val observation = observationRegistry?.let { registry ->
            Observation.createNotStarted("spotify.api.request", registry)
                .lowCardinalityKeyValue("spotify.method", methodName)
                .highCardinalityKeyValue("spotify.user_id", credentials.userId)
                .start()
        }

        return try {
            val result = block(credentials)
            observation?.lowCardinalityKeyValue("spotify.status", "success")
            result
        } catch (e: HttpClientErrorException.Unauthorized) {
            observation?.lowCardinalityKeyValue("spotify.status", "unauthorized")
            logger.warn("Spotify API call $methodName returned 401 Unauthorized for user ${credentials.userId}. Retrying with forced token refresh...")
            val refreshedCredentials = spotifyOAuthService.getActiveToken(credentials.userId, force = true)
            if (refreshedCredentials != null) {
                try {
                    val result = block(refreshedCredentials)
                    observation?.lowCardinalityKeyValue("spotify.retry_status", "success")
                    result
                } catch (retryException: Exception) {
                    observation?.lowCardinalityKeyValue("spotify.retry_status", "failed")
                    observation?.error(retryException)
                    logger.error("Spotify API call $methodName failed after forced token refresh for user ${credentials.userId}", retryException)
                    throw retryException
                }
            } else {
                observation?.lowCardinalityKeyValue("spotify.retry_status", "refresh_failed")
                observation?.error(e)
                logger.error("Failed to refresh Spotify token for user ${credentials.userId} after 401 Unauthorized")
                throw e
            }
        } catch (e: Exception) {
            observation?.lowCardinalityKeyValue("spotify.status", "error")
            observation?.error(e)
            logger.error("Spotify API call $methodName failed: ${e.message}", e)
            throw e
        } finally {
            observation?.stop()
        }
    }
}