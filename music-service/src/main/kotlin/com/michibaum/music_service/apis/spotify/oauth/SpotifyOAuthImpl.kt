package com.michibaum.music_service.apis.spotify.oauth

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.music_service.apis.spotify.SpotifyOAuth
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import io.micrometer.observation.annotation.Observed
import org.springframework.stereotype.Component

@Component
class SpotifyOAuthImpl(
    private val spotifyOAuthService: SpotifyOAuthService,
): SpotifyOAuth {

    @Observed(name = "spotify.oauth.get.credentials")
    override fun getCredentials(principal: JwtAuthentication): SpotifyOAuthCredentials? {
        return spotifyOAuthService.getActiveToken(principal.getUserId())
    }
}