package com.michibaum.authentication_library.security.jwt

import com.michibaum.authentication_library.security.SpecificAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono

class JwtAuthenticationManager(private val jwtService: JwsValidator): SpecificAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication)
            .cast(JwtAuthentication::class.java)
            .filter { jwtAuthentication: JwtAuthentication -> jwtService.validate(jwtAuthentication.token) }
            .map { jwtAuthentication: JwtAuthentication -> jwtAuthentication.apply { isAuthenticated = true } }
            .switchIfEmpty(Mono.error(JwtAuthenticationException("Invalid token.")))
            .cast(Authentication::class.java)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthentication::class.java.isAssignableFrom(authentication)
    }
}
