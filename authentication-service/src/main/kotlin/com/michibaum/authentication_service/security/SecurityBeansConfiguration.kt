package com.michibaum.authentication_service.security

import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.authentication_library.security.jwt.JwsValidator
import com.michibaum.authentication_service.authentication.AuthenticationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SecurityBeansConfiguration {

    @Bean
    fun jwsValidator(authenticationService: AuthenticationService): JwsValidator {
        val authenticationClient = object: AuthenticationClient{
            override fun publicKey(): PublicKeyDto {
                return authenticationService.publicKey
            }
        }
        return JwsValidator(authenticationClient)
    }

}