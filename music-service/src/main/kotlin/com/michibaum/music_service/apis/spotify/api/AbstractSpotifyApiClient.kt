package com.michibaum.music_service.apis.spotify.api

import com.michibaum.music_service.config.properties.ApisProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

abstract class AbstractSpotifyApiClient(restClientBuilder: RestClient.Builder, private val apisProperties: ApisProperties) {

    val client = restClientBuilder
        .baseUrl("https://api.spotify.com/v1")
        .defaultHeaders {
            it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            it.set(HttpHeaders.USER_AGENT, apisProperties.userAgent)
        }
        .build()

}
