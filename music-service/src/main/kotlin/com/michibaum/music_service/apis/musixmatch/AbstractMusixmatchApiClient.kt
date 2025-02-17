package com.michibaum.music_service.apis.musixmatch

import com.michibaum.music_service.config.properties.ApisProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

abstract class AbstractMusixmatchApiClient(restClientBuilder: RestClient.Builder, apisProperties: ApisProperties) {

    val client = restClientBuilder
        .baseUrl("https://api.musixmatch.com/ws/1.1")
        .defaultHeaders {
            it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            it.set(HttpHeaders.USER_AGENT, apisProperties.userAgent)
        }
        .build()

}