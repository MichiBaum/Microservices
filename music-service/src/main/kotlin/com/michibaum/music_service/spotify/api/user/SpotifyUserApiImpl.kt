package com.michibaum.music_service.spotify.api.user

import com.michibaum.music_service.spotify.api.AbstractSpotifyApiClient
import com.michibaum.music_service.spotify.api.user.dtos.SpotifyMeDto
import com.michibaum.music_service.spotify.api.user.dtos.SpotifyTopItemsDto
import com.michibaum.music_service.spotify.api.user.dtos.SpotifyUserDto
import com.michibaum.music_service.spotify.api.user.dtos.TimeRange
import com.michibaum.music_service.spotify.oauth.SpotifyOAuthCredentials
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SpotifyUserApiImpl: AbstractSpotifyApiClient(), SpotifyUserApi {

    override fun myProfile(credentials: SpotifyOAuthCredentials): SpotifyMeDto? {
        // https://developer.spotify.com/documentation/web-api/reference/get-current-users-profile
        return client.get()
            .uri("/me")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .onStatus({ t -> t.value() == 401 }, { Mono.error(Exception()) })
            .onStatus({ t -> t.value() == 403 }, { Mono.error(Exception()) })
            .onStatus({ t -> t.value() == 429 }, { Mono.error(Exception()) })
            .bodyToMono(SpotifyMeDto::class.java)
            .block()
    }

    override fun myTopTracks(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): SpotifyTopItemsDto? {
        // https://developer.spotify.com/documentation/web-api/reference/get-users-top-artists-and-tracks
        return client.get()
            .uri("/me/top/tracks?limit=50&time_range=${timeRange.spotify}")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .onStatus({ t -> t.value() == 401 }, { Mono.error(Exception()) })
            .onStatus({ t -> t.value() == 403 }, { Mono.error(Exception()) })
            .onStatus({ t -> t.value() == 429 }, { Mono.error(Exception()) })
            .bodyToMono(SpotifyTopItemsDto::class.java)
            .block()
    }

    override fun myTopArtists(timeRange: TimeRange, credentials: SpotifyOAuthCredentials): SpotifyTopItemsDto? {
        // https://developer.spotify.com/documentation/web-api/reference/get-users-top-artists-and-tracks
        return client.get()
            .uri("/me/top/artists?limit=50&time_range=${timeRange.spotify}")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .onStatus({ t -> t.value() == 401 }, { Mono.error(Exception()) })
            .onStatus({ t -> t.value() == 403 }, { Mono.error(Exception()) })
            .onStatus({ t -> t.value() == 429 }, { Mono.error(Exception()) })
            .bodyToMono(SpotifyTopItemsDto::class.java)
            .block()
    }

    override fun profileFor(userId: String, credentials: SpotifyOAuthCredentials): SpotifyUserDto? {
        return client.get()
            .uri("/users/$userId")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .onStatus({ t -> t.value() == 401 }, { Mono.error(Exception()) })
            .onStatus({ t -> t.value() == 403 }, { Mono.error(Exception()) })
            .onStatus({ t -> t.value() == 429 }, { Mono.error(Exception()) })
            .bodyToMono(SpotifyUserDto::class.java)
            .block()
    }
}