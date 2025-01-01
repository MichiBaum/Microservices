package com.michibaum.fitness_service.fitbit.oauth

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.fitness_service.fitbit.FitbitOAuth
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.Instant
import java.util.*

@Component
class FitbitOAuthImpl(
    private val fitbitOAuthService: FitbitOAuthService,
    private val fitbitOAuthProperties: FitbitOAuthProperties
): FitbitOAuth {

    private fun refreshToken(principal: JwtAuthentication): FitbitOAuthCredentials {
        val credentials = fitbitOAuthService.activeCredentialsByUser(principal.getUserId()) ?: throw Exception("")

        val clientAndSecret = fitbitOAuthProperties.clientId + ":" + fitbitOAuthProperties.clientSecret
        val authBasic = Base64.getUrlEncoder().withoutPadding().encodeToString(clientAndSecret.encodeToByteArray())

        val client = RestClient.builder()
            .baseUrl("https://api.fitbit.com")
            .defaultHeaders {
                it.set("Authorization", "Basic $authBasic")
                it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                it.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            }
            .build()
        val body = FitbitRefreshBodyDto("refresh_token", credentials.refreshToken)
        val response = client
            .post()
            .uri("/oauth2/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body) // TODO needs testing if this works
            .retrieve()
            .onStatus({ t -> t.is4xxClientError }, { _, _ -> }) // TODO https://dev.fitbit.com/build/reference/web-api/troubleshooting-guide/error-messages/#authorization-errors
            .body(FitbitOAuthCredentialsDto::class.java)

        if(response == null){
            throw Exception("Fitbit OAuth refresh access token returned null")
        }

        return fitbitOAuthService.saveNewAndDeactivateOld(response, credentials)
    }

    /**
     * Retrieves the Fitbit OAuth credentials for a given user, refreshing the token if necessary.
     *
     * @param principal The principal containing user authentication details.
     * @return The Fitbit OAuth credentials or null if no active credentials are found.
     */
    override fun getCredentials(principal: JwtAuthentication): FitbitOAuthCredentials? {
        val credentials = fitbitOAuthService.activeCredentialsByUser(principal.getUserId()) ?: return null

        // Now, but with a buffer of 5 minutes, so that there should not occur an error by the access token expiring during the request
        val now = Instant.now().minusSeconds(300)
        if(credentials.validUntil.isAfter(now)){
            return refreshToken(principal)
        }

        return credentials
    }

}