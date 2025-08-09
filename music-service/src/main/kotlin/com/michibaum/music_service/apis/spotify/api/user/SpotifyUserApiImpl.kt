package com.michibaum.music_service.apis.spotify.api.user

import com.michibaum.music_service.apis.spotify.api.AbstractSpotifyApiClient
import com.michibaum.music_service.apis.spotify.api.user.dtos.SpotifyMeDto
import com.michibaum.music_service.apis.spotify.api.user.dtos.SpotifyTopItemsDto
import com.michibaum.music_service.apis.spotify.api.user.dtos.SpotifyUserDto
import com.michibaum.music_service.apis.spotify.api.user.dtos.TimeRange
import com.michibaum.music_service.config.properties.ApisProperties
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class SpotifyUserApiImpl(
    restClientBuilder: RestClient.Builder,
    apisProperties: ApisProperties
): AbstractSpotifyApiClient(restClientBuilder, apisProperties), SpotifyUserApi {

    override fun myProfile(credentials: SpotifyOAuthCredentials): SpotifyMeDto? {
        // https://developer.spotify.com/documentation/web-api/reference/get-current-users-profile
        return client.get()
            .uri("/me")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .onStatus({ t -> t.value() == 401 }, { _, _ -> })
            .onStatus({ t -> t.value() == 403 }, { _, _ -> })
            .onStatus({ t -> t.value() == 429 }, { _, _ -> })
            .body(SpotifyMeDto::class.java)
    }

    override fun myTopTracks(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): SpotifyTopItemsDto? {
        // https://developer.spotify.com/documentation/web-api/reference/get-users-top-artists-and-tracks
        return client.get()
            .uri("/me/top/tracks?limit=50&time_range=${timeRange.spotify}")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .onStatus({ t -> t.value() == 401 }, { _, _ -> })
            .onStatus({ t -> t.value() == 403 }, { _, _ -> })
            .onStatus({ t -> t.value() == 429 }, { _, _ -> })
            .body(SpotifyTopItemsDto::class.java)
    }

    override fun myTopArtists(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): SpotifyTopItemsDto? {
        // https://developer.spotify.com/documentation/web-api/reference/get-users-top-artists-and-tracks
        return client.get()
            .uri("/me/top/artists?limit=50&time_range=${timeRange.spotify}")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .onStatus({ t -> t.value() == 401 }, { _, _ -> })
            .onStatus({ t -> t.value() == 403 }, { _, _ -> })
            .onStatus({ t -> t.value() == 429 }, { _, _ -> })
            .body(SpotifyTopItemsDto::class.java)
    }

    override fun profileFor(userId: String, credentials: SpotifyOAuthCredentials): SpotifyUserDto? {
        return client.get()
            .uri("/users/$userId")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .onStatus({ t -> t.value() == 401 }, { _, _ -> })
            .onStatus({ t -> t.value() == 403 }, { _, _ -> })
            .onStatus({ t -> t.value() == 429 }, { _, _ -> })
            .body(SpotifyUserDto::class.java)
    }
}