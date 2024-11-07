package com.michibaum.fitness_service.fitbit.oauth

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.fitness_service.fitbit.FitbitOAuth
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@Component
class FitbitOAuthImpl(
    val fitbitOAuthService: FitbitOAuthService,
    val fitbitOAuthProperties: FitbitOAuthProperties
): FitbitOAuth {

    override fun refreshToken(principal: JwtAuthentication): FitbitOAuthCredentials {
        val credentials = fitbitOAuthService.activeCredentialsByUser("") ?: throw Exception("")

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
                    .with("refresh_token", credentials.refreshToken)
            )
            .retrieve()
            .onStatus({ t -> t.is4xxClientError }, { Mono.error(Exception()) }) // TODO https://dev.fitbit.com/build/reference/web-api/troubleshooting-guide/error-messages/#authorization-errors
            .bodyToMono(FitbitOAuthCredentialsDto::class.java)
            .block()

        if(response == null){
            throw Exception("Fitbit OAuth refresh access token returned null")
        }

        return fitbitOAuthService.saveNewAndDeactivateOld(response, credentials)
    }

    /**
     * Fetches the active Fitbit OAuth credentials for the given user, represented by the provided principal.
     * If the credentials are expired, attempts to refresh the token and returns the new credentials.
     *
     * @param principal The JWT authentication token containing the user's details.
     * @return The active or refreshed Fitbit OAuth credentials, or null if no valid credentials are found.
     */
    override fun getCredentials(principal: JwtAuthentication): FitbitOAuthCredentials? {
        val credentials = fitbitOAuthService.activeCredentialsByUser(principal.getUserId()) ?: return null

        // Now, but with a buffer of 5 minutes, so that there can be no error by the access token expiring during the request
        val now = Instant.now().minusSeconds(300)
        if(credentials.validUntil.isBefore(now)){
            return refreshToken(principal)
        }

        return credentials
    }

}