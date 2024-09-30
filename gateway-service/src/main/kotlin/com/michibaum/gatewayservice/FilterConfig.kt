package com.michibaum.gatewayservice

import com.michibaum.authentication_library.AuthenticationClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

@Configuration
class FilterConfig {

    @Bean
    fun jwsValidator(@Lazy authenticationClient: AuthenticationClient): JWSValidator{ // Cycling Dependencies
        return JWSValidator(authenticationClient)
    }

    @Bean
    fun authenticationFilter(jwsValidator: JWSValidator): AuthenticationFilter {
        return AuthenticationFilter(jwsValidator)
    }

    @Bean
    fun authorizationPreFilter(): AuthorizationPreFilter {
        return AuthorizationPreFilter()
    }

}