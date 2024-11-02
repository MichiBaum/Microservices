package com.michibaum.fitness_service.fitbit.oauth

import com.michibaum.authentication_library.security.netty.JwtToken
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

@RestController
class FitbitOAuthController(
    val fitbitOAuthService: FitbitOAuthService,
    val fitbitOAuthProperties: FitbitOAuthProperties
) {

    @GetMapping("/api/fitbit/token")
    fun token(principal: JwtToken): FitbitLoginDto {
        val fitbitOAuthData = fitbitOAuthService.generateData(principal)

        val redirectUri = URLEncoder.encode("https://fitness.michibaum.ch/api/fitbit/auth", StandardCharsets.UTF_8)

        // Construct the URL
        val url = "https://www.fitbit.com/oauth2/authorize?" +
                "response_type=code" +
                "&client_id=${fitbitOAuthProperties.clientId}" +
                "&scope=activity+cardio_fitness+electrocardiogram+heartrate+irregular_rhythm_notifications+location+nutrition+oxygen_saturation+profile+respiratory_rate+settings+sleep+social+temperature+weight" +
                "&code_challenge=${fitbitOAuthData.codeChallenge}" +
                "&code_challenge_method=S256" +
                "&state=${fitbitOAuthData.state}" +
                "&redirect_uri=$redirectUri"

        return FitbitLoginDto(
            clientId = fitbitOAuthProperties.clientId,
            codeChallenge = fitbitOAuthData.codeChallenge,
            state = fitbitOAuthData.state,
            url = url
        )

    }

    @GetMapping("/api/fitbit/auth")
    fun authorizationCode(@RequestParam code: String, @RequestParam state: String, exchange: ServerWebExchange){
        val fitbitOAuthData = fitbitOAuthService.findByState(state) ?: throw Exception("")

        val clientAndSecret = fitbitOAuthProperties.clientId + ":" + fitbitOAuthProperties.clientSecret
        val authBasic = Base64.getUrlEncoder().withoutPadding().encodeToString(clientAndSecret.encodeToByteArray())

        val response = WebClient.builder()
            .baseUrl("https://api.fitbit.com/oauth2/token")
            .defaultHeaders { 
                it.set("Authorization", "Basic $authBasic")
                it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            }
            .build()
            .post()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters.fromFormData("client_id", fitbitOAuthProperties.clientId)
                    .with("grant_type", "authorization_code")
                    .with("code", code)
                    .with("code_verifier", fitbitOAuthData.codeVerifier)
                    .with("redirect_uri", "https://fitness.michibaum.ch/api/fitbit/auth")
            )
            .retrieve()
            .bodyToMono(FitbitOAuthCredentialsDto::class.java)
            .block()

        if(response == null){
            throw Exception("Fitbit OAuth exchange for access token returned null")
        }

        fitbitOAuthService.save(response, fitbitOAuthData)

        return exchange.response.let {
            it.statusCode = HttpStatus.FOUND
            it.headers.location = URI("https://michibaum.ch/fitness")
            it.setComplete()
        }

    }

}