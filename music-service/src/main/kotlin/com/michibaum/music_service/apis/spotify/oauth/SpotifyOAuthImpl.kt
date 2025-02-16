package com.michibaum.music_service.apis.spotify.oauth

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.music_service.apis.spotify.SpotifyOAuth
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class SpotifyOAuthImpl(
    private val spotifyOAuthCredentialsRepository: SpotifyOAuthCredentialsRepository,
    private val spotifyOAuthService: SpotifyOAuthService,
): SpotifyOAuth {

    private fun refreshToken(principal: JwtAuthentication): SpotifyOAuthCredentials? {
        TODO("Not yet implemented")
    }

    /**
     * Retrieves the Spotify OAuth credentials for the given principal.
     *
     * The method first searches for active credentials for the user associated with the provided principal.
     * If no active credentials are found, it returns null. If the credentials are found but are close to expiring
     * (within a 5-minute buffer), it attempts to refresh the token before returning the new credentials.
     *
     * @param principal The JWT authentication token containing user details.
     * @return The Spotify OAuth credentials for the user, or null if no active credentials are found or an error occurs.
     */
    override fun getCredentials(principal: JwtAuthentication): SpotifyOAuthCredentials? {
        val credentials = spotifyOAuthService.activeCredentialsByUser(principal.getUserId()) ?: return null

        // Now, but with a buffer of 5 minutes, so that there should not occur an error by the access token expiring during the request
        val now = Instant.now().minusSeconds(300)
        if(credentials.validUntil.isAfter(now)){
            return refreshToken(principal)
        }

        return credentials
    }
}