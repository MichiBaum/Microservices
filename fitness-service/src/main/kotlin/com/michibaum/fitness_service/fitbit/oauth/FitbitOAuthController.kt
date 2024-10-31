package com.michibaum.fitness_service.fitbit.oauth

import com.michibaum.authentication_library.security.netty.JwtToken
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@RestController
class FitbitOAuthController(
    val fitbitOAuthService: FitbitOAuthService,
    val fitbitOAuthProperties: FitbitOAuthProperties
) {

    private val logger = LoggerFactory.getLogger(FitbitOAuthController::class.java)

    @GetMapping("/api/fitbit/token")
    fun token(principal: JwtToken): String {
        val fitbitOAuthData = fitbitOAuthService.generateData(principal)

        // Construct the URL
        return "https://www.fitbit.com/oauth2/authorize?" +
                "response_type=code" +
                "&client_id=${fitbitOAuthProperties.clientId}" +
                "&scope=activity+cardio_fitness+electrocardiogram+heartrate+irregular_rhythm_notifications+location+nutrition+oxygen_saturation+profile+respiratory_rate+settings+sleep+social+temperature+weight" +
                "&code_challenge=${fitbitOAuthData.codeChallenge}" +
                "&code_challenge_method=S256" +
                "&state=${fitbitOAuthData.state}" +
                "&redirect_uri=https%3A%2F%2Fmichibaum.ch%2Fapi%2Ffitbit%2Fauth"

    }

    @GetMapping("/api/fitbit/auth")
    fun authorizationCode(@RequestParam code: String, @RequestParam state: String){
        // https://dev.fitbit.com/build/reference/web-api/troubleshooting-guide/oauth2-tutorial/?clientEncodedId=23PQKW&redirectUri=https://michibaum.ch&applicationType=SERVER

        val fitbitOAuthData = fitbitOAuthService.findByState(state) ?: throw Exception("")

        val clientAndSecret = fitbitOAuthProperties.clientId + ":" + fitbitOAuthProperties.clientSecret
        val authBasic = Base64.getUrlEncoder().withoutPadding().encodeToString(clientAndSecret.encodeToByteArray())

        val webClient: WebClient = WebClient.builder()
            .baseUrl("https://api.fitbit.com/oauth2/token")
            .defaultHeaders { 
                it.set("Authorization", "Basic $authBasic")
                it.set("Content-Type", "application/x-www-form-urlencoded")
            }
            .build()


        val response = webClient.post()
            .bodyValue(
                "client_id=${fitbitOAuthProperties.clientId}" +
                        "&grant_type=authorization_code" +
                        "&redirect_uri=https%3A%2F%2Fmichibaum.ch%2Fapi%2Ffitbit%2Fauth" +
                        "&code=$code" +
                        "&code_verifier=${fitbitOAuthData.codeVerifier}"
            )
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        logger.info("Response: $response")
        
        // exchange the authorization code for a pair of Access and Request Tokens
        // An example call using curl:
        //curl -i -X POST \
        //https://api.fitbit.com/oauth2/token \
        // -H 'Authorization: Basic ${authBasic}' \
        // -H 'Content-Type: application/x-www-form-urlencoded' \
        // --data "client_id=${fitbitOAuthProperties.clientId}" \
        // --data "grant_type=authorization_code" \
        // --data "redirect_uri=https%3A%2F%2Fmichibaum.ch" \
        // --data "code=${code}" \
        // --data "code_verifier=${fitbitOAuthData.codeVerifier}"

    }

}