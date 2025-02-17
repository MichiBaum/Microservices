package com.michibaum.music_service.apis.musixmatch

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

abstract class AbstractMusixmatchApiClient {

    val client = RestClient.builder()
        .baseUrl("https://api.musixmatch.com/ws/1.1")
        .defaultHeaders {
            it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        }
        .build()

}