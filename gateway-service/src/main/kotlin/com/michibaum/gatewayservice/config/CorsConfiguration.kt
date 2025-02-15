package com.michibaum.gatewayservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
@EnableConfigurationProperties(value = [CorsProperties::class])
class CorsConfiguration(
    private val corsProperties: CorsProperties
): WebMvcConfigurer {

    override fun addCorsMappings(registry: org.springframework.web.servlet.config.annotation.CorsRegistry) {
        registry.addMapping("/**")
            .allowedMethods("*")
            .allowedOriginPatterns(*corsProperties.allowedOriginPatterns.toTypedArray())
            .allowedHeaders("*")
            .allowCredentials(true)
    }

}

@ConfigurationProperties(prefix = "cors")
data class CorsProperties(
    val allowedOriginPatterns: List<String>
)