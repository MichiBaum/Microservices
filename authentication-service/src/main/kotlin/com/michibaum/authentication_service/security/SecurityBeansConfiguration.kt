package com.michibaum.authentication_service.security

import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.authentication_library.public_endpoints.PublicEndpointResolver
import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.authentication_library.security.ServletDelegateAuthenticationManager
import com.michibaum.authentication_library.security.SpecificAuthenticationManager
import com.michibaum.authentication_library.security.basic.BasicAuthenticationManager
import com.michibaum.authentication_library.security.basic.CredentialsValidator
import com.michibaum.authentication_library.security.basic.BasicAuthenticationConverter
import com.michibaum.authentication_library.security.jwt.JwsValidator
import com.michibaum.authentication_library.security.jwt.JwtAuthenticationManager
import com.michibaum.authentication_library.security.jwt.JwtAuthenticationConverter
import com.michibaum.authentication_service.authentication.AuthenticationService
import com.michibaum.authentication_starter.AdminServiceCredentials
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.web.authentication.AuthenticationConverter

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