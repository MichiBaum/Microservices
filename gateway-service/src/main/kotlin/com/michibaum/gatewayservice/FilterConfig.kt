package com.michibaum.gatewayservice

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterConfig {

    @Bean
    fun authenticationFilter(): AuthenticationFilter {
        return AuthenticationFilter()
    }

    @Bean
    fun authorizationPreFilter(): AuthorizationPreFilter {
        return AuthorizationPreFilter()
    }

}