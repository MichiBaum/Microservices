package com.michibaum.music_service.apis.spotify.api.user

import com.michibaum.music_service.apis.spotify.api.AbstractSpotifyApiClient
import com.michibaum.music_service.apis.spotify.generated.model.PagingPlaylistObject
import com.michibaum.music_service.apis.spotify.generated.model.PagingPlaylistTrackObject
import com.michibaum.music_service.apis.spotify.generated.model.PlaylistObject
import com.michibaum.music_service.apis.spotify.generated.model.PlaylistTrackObject
import com.michibaum.music_service.apis.spotify.generated.model.SimplifiedPlaylistObject
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
class SpotifyPlaylistApiImpl(
    restClientBuilder: RestClient.Builder,
    apisProperties: ApisProperties,
    private val spotifyOAuthService: SpotifyOAuthService,
    private val observationRegistry: ObservationRegistry? = null,
): AbstractSpotifyApiClient(restClientBuilder, apisProperties), SpotifyPlaylistApi {

    private val logger = LoggerFactory.getLogger(SpotifyPlaylistApiImpl::class.java)

    @Observed(name = "spotify.api.my.playlists")
    override fun myPlaylists(credentials: SpotifyOAuthCredentials): List<SimplifiedPlaylistObject> {
        return tryCall("myPlaylists", credentials) { creds ->
            client.get()
                .uri("/me/playlists")
                .headers {
                    it.setBearerAuth(creds.accessToken)
                }
                .retrieve()
                .body(PagingPlaylistObject::class.java)
                ?.items
        } ?: emptyList()
    }

    @Observed(name = "spotify.api.get.playlist")
    override fun getPlaylist(playlistId: String, credentials: SpotifyOAuthCredentials): PlaylistObject? {
        return tryCall("getPlaylist", credentials) { creds ->
            client.get()
                .uri("/playlists/{playlistId}", playlistId)
                .headers {
                    it.setBearerAuth(creds.accessToken)
                }
                .retrieve()
                .body(PlaylistObject::class.java)
        }
    }

    @Observed(name = "spotify.api.get.playlist.tracks")
    override fun getPlaylistTracks(playlistId: String, credentials: SpotifyOAuthCredentials): List<PlaylistTrackObject> {
        return tryCall("getPlaylistTracks", credentials) { creds ->
            client.get()
                .uri("/playlists/{playlistId}/items", playlistId)
                .headers {
                    it.setBearerAuth(creds.accessToken)
                }
                .retrieve()
                .body(PagingPlaylistTrackObject::class.java)
                ?.items
        } ?: emptyList()
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
