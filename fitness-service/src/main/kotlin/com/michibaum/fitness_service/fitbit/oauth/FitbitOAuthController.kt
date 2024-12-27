package com.michibaum.fitness_service.fitbit.oauth

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import reactor.core.publisher.Mono
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

@RestController
class FitbitOAuthController(
    private val fitbitOAuthService: FitbitOAuthService,
    private val fitbitOAuthProperties: FitbitOAuthProperties
) {

    private final val client: RestClient

    init {
        val clientAndSecret = fitbitOAuthProperties.clientId + ":" + fitbitOAuthProperties.clientSecret
        val authBasic = Base64.getUrlEncoder().withoutPadding().encodeToString(clientAndSecret.encodeToByteArray())

        client = RestClient.builder()
            .baseUrl("https://api.fitbit.com")
            .defaultHeaders {
                it.set("Authorization", "Basic $authBasic")
                it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                it.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            }
            .build()
    }

    @GetMapping("/api/fitbit/token")
    fun token(principal: JwtAuthentication): FitbitLoginDto {
        val oAuthData = fitbitOAuthService.generateData(principal)

        val redirectUri = URLEncoder.encode("https://fitness.michibaum.ch/api/fitbit/auth", StandardCharsets.UTF_8)

        // Construct the URL
        val scopes = "activity+cardio_fitness+electrocardiogram+heartrate+irregular_rhythm_notifications+location+nutrition+oxygen_saturation+profile+respiratory_rate+settings+sleep+social+temperature+weight"
        val url = "https://www.fitbit.com/oauth2/authorize?" +
                "response_type=code" +
                "&client_id=${fitbitOAuthProperties.clientId}" +
                "&scope=$scopes" +
                "&code_challenge=${oAuthData.codeChallenge}" +
                "&code_challenge_method=S256" +
                "&state=${oAuthData.state}" +
                "&redirect_uri=$redirectUri"

        return FitbitLoginDto(
            clientId = fitbitOAuthProperties.clientId,
            codeChallenge = oAuthData.codeChallenge,
            state = oAuthData.state,
            url = url
        )

    }

    @GetMapping("/api/fitbit/auth")
    fun authorizationCode(@RequestParam code: String, @RequestParam state: String){
        val oAuthData = fitbitOAuthService.findByState(state) ?: throw Exception("No oAuthData found by state $state")

        val response = client
            .post()
            .uri("/oauth2/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters.fromFormData("client_id", fitbitOAuthProperties.clientId)
                    .with("grant_type", "authorization_code")
                    .with("code", code)
                    .with("code_verifier", oAuthData.codeVerifier)
                    .with("redirect_uri", "https://fitness.michibaum.ch/api/fitbit/auth")
            )
            .retrieve()
            .onStatus({ t -> t.is4xxClientError }, { _, _ -> }) // TODO https://dev.fitbit.com/build/reference/web-api/troubleshooting-guide/error-messages/#authorization-errors
            .body(FitbitOAuthCredentialsDto::class.java)

        if(response == null){
            throw Exception("Fitbit OAuth exchange for access token returned null")
        }

        val credentials = fitbitOAuthService.save(response, oAuthData)

        // TODO return something

    }

}