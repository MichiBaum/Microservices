package com.michibaum.fitness_service.fitbit.oauth

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.fitness_service.fitbit.FitbitOAuth
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.*

@Component
class FitbitOAuthImpl(
    val fitbitOAuthService: FitbitOAuthService,
    val fitbitOAuthProperties: FitbitOAuthProperties
): FitbitOAuth {

    override fun refreshToken(principal: JwtAuthentication): FitbitOAuthCredentials {
        val fitbitOAuthData = fitbitOAuthService.credentialsByUser("") ?: throw Exception("")

        val clientAndSecret = fitbitOAuthProperties.clientId + ":" + fitbitOAuthProperties.clientSecret
        val authBasic = Base64.getUrlEncoder().withoutPadding().encodeToString(clientAndSecret.encodeToByteArray())

        val response = WebClient.builder()
            .baseUrl("https://api.fitbit.com/oauth2/token")
            .defaultHeaders {
                it.set("Authorization", "Basic $authBasic")
                it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                it.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            }
            .build()
            .post()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters.fromFormData("grant_type", "refresh_token")
                    .with("refresh_token", fitbitOAuthData.refreshToken)
            )
            .retrieve()
            .onStatus({ t -> t.is4xxClientError }, { Mono.error(Exception()) }) // TODO https://dev.fitbit.com/build/reference/web-api/troubleshooting-guide/error-messages/#authorization-errors
            .bodyToMono(FitbitOAuthCredentialsDto::class.java)
            .block()

        if(response == null){
            throw Exception("Fitbit OAuth exchange for access token returned null")
        }

        return fitbitOAuthService.saveNewAndDeactivateOld(response, fitbitOAuthData)
    }

    override fun getCredentials(principal: JwtAuthentication): FitbitOAuthCredentials? {
        return fitbitOAuthService.credentialsByUser(principal.getUserId())
    }

}