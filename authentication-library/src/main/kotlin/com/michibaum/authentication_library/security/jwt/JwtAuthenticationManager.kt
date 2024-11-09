package com.michibaum.authentication_library.security.jwt

import com.michibaum.authentication_library.JwsValidationFailed
import com.michibaum.authentication_library.JwsValidationSuccess
import com.michibaum.authentication_library.security.SpecificAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono

class JwtAuthenticationManager(private val jwtService: JwsValidator): SpecificAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication)
            .cast(JwtAuthentication::class.java)
            .map { jwtAuthentication: JwtAuthentication ->
                val result = jwtService.validate(jwtAuthentication.token)
                when (result) {
                    is JwsValidationSuccess -> jwtAuthentication.apply { isAuthenticated = true }
                    is JwsValidationFailed -> jwtAuthentication.apply { isAuthenticated = false }
                }
            }
            .switchIfEmpty(Mono.error(JwtAuthenticationException("")))
            .cast(Authentication::class.java)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthentication::class.java.isAssignableFrom(authentication)
    }
}
