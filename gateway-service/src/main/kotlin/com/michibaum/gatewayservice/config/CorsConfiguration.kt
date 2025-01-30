package com.michibaum.gatewayservice.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
@Profile("dev")
class CorsConfiguration: WebMvcConfigurer {

    override fun addCorsMappings(registry: org.springframework.web.servlet.config.annotation.CorsRegistry) {
        registry.addMapping("/**")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedOrigins("http://michibaum.ch:4200", "http://michibaum.com:4200", "http://michibaum.eu:4200")
            .allowedHeaders("*")
            .allowCredentials(true)
    }

}