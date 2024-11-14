package com.michibaum.music_service.spotify.api

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

open class AbstractSpotifyApiClient {

    val client = WebClient.builder()
        .baseUrl("https://api.spotify.com/v1")
        .defaultHeaders {
            it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        }
        .codecs { configurer ->
            configurer.defaultCodecs()
                .maxInMemorySize(10000000)
        }
        .build()

}
