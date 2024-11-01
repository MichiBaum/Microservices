package com.michibaum.authentication_library.security.netty

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono

class JwtAuthenticationManager(private val jwtService: JwsValidator): ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication)
            .cast(JwtToken::class.java)
            .filter { jwtToken: JwtToken -> jwtService.validate(jwtToken.token) }
            .map { jwtToken: JwtToken ->
                jwtToken.isAuthenticated = true
                jwtToken
            }
            .switchIfEmpty(Mono.error(JwtAuthenticationException("Invalid token.")))
            .cast(Authentication::class.java)
    }
}
