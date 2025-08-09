package com.michibaum.music_service.apis.musicbrainz

import com.michibaum.music_service.apis.ApiResult
import com.michibaum.music_service.config.properties.ApisProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

abstract class AbstractMusicBrainzApiClient(restClientBuilder: RestClient.Builder, apisProperties: ApisProperties) {

    val client = restClientBuilder
        .baseUrl("https://api.musixmatch.com/ws/1.1")
        .defaultHeaders {
            it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            it.set(HttpHeaders.USER_AGENT, apisProperties.userAgent)
        }
        .build()

    fun <T : Any> executeApiCall(call: () -> T?): ApiResult<T> {
        return try {
            val result = call()
            if (result != null) {
                ApiResult.Success(result)
            } else {
                ApiResult.NotFound
            }
        } catch (ex: HttpClientErrorException) {
            when (ex.statusCode.value()) {
                404 -> ApiResult.NotFound
                429 -> ApiResult.QuotaLimitExceeded
                else -> ApiResult.ApiError(
                    ex.responseBodyAsString,
                    ex.statusCode.value(),
                    ex
                )
            }
        } catch (ex: RestClientException) {
            ApiResult.ApiError(
                ex.message ?: "Unknown error",
                500,
                ex
            )
        } catch (ex: Exception) {
            ApiResult.ApiError(
                ex.message ?: "Unknown error",
                500,
                ex
            )
        }
    }
    
}