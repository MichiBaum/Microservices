package com.michibaum.authentication_service.config

import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.authentication_library.security.jwt.JwsValidator
import com.michibaum.authentication_service.app.authentication.AuthenticationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion
import org.springframework.security.crypto.password.PasswordEncoder

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

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        BCryptPasswordEncoder(BCryptVersion.`$2A`, 10, null)

}