package com.michibaum.music_service.spotify

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.music_service.spotify.oauth.SpotifyOAuthCredentials

interface SpotifyOAuth {

    fun getCredentials(principal: JwtAuthentication): SpotifyOAuthCredentials?

}