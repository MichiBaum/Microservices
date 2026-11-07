package com.michibaum.authentication_service.config

import com.michibaum.authentication_service.app.authentication.AuthenticationService
import feign.RequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignClientConfiguration(
    private val authenticationService: AuthenticationService
) {

    @Bean
    fun feignRequestInterceptor(): RequestInterceptor = RequestInterceptor { template ->
        val token = authenticationService.generateInternalServiceJWS()
        template.header("Authorization", "Bearer $token")
    }

}
