package com.michibaum.music_service.apis.spotify.api

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

open class AbstractSpotifyApiClient {

    val client = RestClient.builder()
        .baseUrl("https://api.spotify.com/v1")
        .defaultHeaders {
            it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        }
        .build()

}
