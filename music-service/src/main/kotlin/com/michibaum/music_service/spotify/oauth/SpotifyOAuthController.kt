package com.michibaum.music_service.spotify.oauth

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SpotifyOAuthController { // https://developer.spotify.com/documentation/web-api/tutorials/code-flow

    @GetMapping("/api/spotify/token")
    fun token(principal: JwtAuthentication) {

    }

    @GetMapping("/api/fitbit/auth")
    fun authorizationCode(@RequestParam code: String, @RequestParam state: String) {

    }

}