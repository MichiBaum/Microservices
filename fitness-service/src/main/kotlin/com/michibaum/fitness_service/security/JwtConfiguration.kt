package com.michibaum.fitness_service.security

import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.security.netty.JwsValidator
import com.michibaum.authentication_library.security.netty.JwtAuthenticationConverter
import com.michibaum.authentication_library.security.netty.JwtAuthenticationManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

@Configuration
class JwtConfiguration {

    @Bean
    fun jwsValidator(@Lazy authenticationClient: AuthenticationClient): JwsValidator =
        JwsValidator(authenticationClient)

    @Bean
    fun jwtAuthenticationManager(jwsValidator: JwsValidator): JwtAuthenticationManager =
        JwtAuthenticationManager(jwsValidator)

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter =
        JwtAuthenticationConverter()

}