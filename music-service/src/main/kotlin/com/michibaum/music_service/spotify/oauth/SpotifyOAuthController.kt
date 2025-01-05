package com.michibaum.music_service.spotify.oauth

import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

@RestController
class SpotifyOAuthController(
    private val spotifyOAuthService: SpotifyOAuthService,
    private val spotifyOAuthProperties: SpotifyOAuthProperties
) { // https://developer.spotify.com/documentation/web-api/tutorials/code-flow

    private final val client: RestClient

    init {
        val clientAndSecret = spotifyOAuthProperties.clientId + ":" + spotifyOAuthProperties.clientSecret
        val authBasic = Base64.getUrlEncoder().withoutPadding().encodeToString(clientAndSecret.encodeToByteArray())

        client = RestClient.builder()
            .baseUrl("https://accounts.spotify.com")
            .defaultHeaders {
                it.set("Authorization", "Basic $authBasic")
                it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                it.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            }
            .build()
    }

    @GetMapping("/api/spotify/token")
    fun token(principal: JwtAuthentication): SpotifyLoginDto {
        val oAuthData = spotifyOAuthService.generateData(principal)

        val redirectUri = URLEncoder.encode("https://music.michibaum.ch/api/spotify/auth", StandardCharsets.UTF_8)
        val scopes = "user-read-email user-read-private " + // User
                "ugc-image-upload " + // Images
                "user-read-playback-state user-modify-playback-state user-read-currently-playing " + // Spotify Connect
                "app-remote-control streaming " + // Playback
                "playlist-read-private playlist-read-collaborative playlist-modify-private playlist-modify-public " + // Playlists
                "user-follow-modify user-follow-read " + // Follow
                "user-read-playback-position user-top-read user-read-recently-played " + // Listening History
                "user-library-modify user-library-read" // Library
        val url = "https://accounts.spotify.com/authorize?" +
            "response_type=code" +
            "&client_id=${spotifyOAuthProperties.clientId}" +
            "&scope=$scopes" +
            "&redirect_uri=$redirectUri" +
            "&state=${oAuthData.state}"

        return SpotifyLoginDto(
            clientId = spotifyOAuthProperties.clientId,
            state = oAuthData.state,
            url = url
        )
    }

    @PublicEndpoint
    @GetMapping("/api/spotify/auth")
    fun authorizationCode(@RequestParam code: String, @RequestParam state: String) {
        val oAuthData = spotifyOAuthService.findByState(state) ?: throw Exception("No oAuthData found by state $state")

        val data = SpotifyOAuthDto(code, "authorization_code", "https://music.michibaum.ch/api/spotify/auth")
        val response = client
            .post()
            .uri("/api/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(data)// TODO needs testing
            .retrieve()
            .onStatus({ t -> t.is4xxClientError }, { _, _ -> })
            .body(SpotifyOAuthCredentialsDto::class.java)

        if(response == null){
            throw Exception("Spotify OAuth exchange for access token returned null")
        }

        val credentials = spotifyOAuthService.save(response, oAuthData)

        // TODO return something
    }

}