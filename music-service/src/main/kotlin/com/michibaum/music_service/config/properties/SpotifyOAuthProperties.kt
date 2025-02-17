package com.michibaum.music_service.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "spotify.oauth")
data class SpotifyOAuthProperties(
    var clientId: String = "",
    var clientSecret: String = ""
)