package com.michibaum.music_service.apis.spotify.oauth

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.music_service.apis.spotify.SpotifyOAuth
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import org.springframework.stereotype.Component

@Component
class SpotifyOAuthImpl(
    private val spotifyOAuthService: SpotifyOAuthService,
): SpotifyOAuth {

    override fun getCredentials(principal: JwtAuthentication): SpotifyOAuthCredentials? {
        return spotifyOAuthService.getActiveToken(principal.getUserId())
    }
}