package com.michibaum.music_service.apis.spotify.api.user

import com.michibaum.music_service.apis.spotify.api.AbstractSpotifyApiClient
import com.michibaum.music_service.apis.spotify.api.user.dtos.TimeRange
import com.michibaum.music_service.apis.spotify.generated.model.GetUsersTopArtistsAndTracks200Response
import com.michibaum.music_service.apis.spotify.generated.model.PrivateUserObject
import com.michibaum.music_service.apis.spotify.generated.model.PublicUserObject
import com.michibaum.music_service.config.properties.ApisProperties
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class SpotifyUserApiImpl(
    restClientBuilder: RestClient.Builder,
    apisProperties: ApisProperties
): AbstractSpotifyApiClient(restClientBuilder, apisProperties), SpotifyUserApi {

    override fun myProfile(credentials: SpotifyOAuthCredentials): PrivateUserObject? {
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
            .body(PrivateUserObject::class.java)
    }

    override fun myTopTracks(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): GetUsersTopArtistsAndTracks200Response? {
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
            .body(GetUsersTopArtistsAndTracks200Response::class.java)
    }

    override fun myTopArtists(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): GetUsersTopArtistsAndTracks200Response? {
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
            .body(GetUsersTopArtistsAndTracks200Response::class.java)
    }

    override fun profileFor(userId: String, credentials: SpotifyOAuthCredentials): PublicUserObject? {
        return client.get()
            .uri("/users/$userId")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .onStatus({ t -> t.value() == 401 }, { _, _ -> })
            .onStatus({ t -> t.value() == 403 }, { _, _ -> })
            .onStatus({ t -> t.value() == 429 }, { _, _ -> })
            .body(PublicUserObject::class.java)
    }
}