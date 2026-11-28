package com.michibaum.gatewayservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@Configuration
@EnableWebMvc
@EnableConfigurationProperties(value = [CorsProperties::class])
class CorsConfiguration

@ConfigurationProperties(prefix = "cors")
data class CorsProperties(
    val baseAllowedOriginPatterns: List<String> = emptyList(),
    val allowedOriginPatterns: List<String> = emptyList()
) {
    fun effectiveBaseAllowedOriginPatterns(): List<String> {
        return (baseAllowedOriginPatterns + allowedOriginPatterns)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
    }
}