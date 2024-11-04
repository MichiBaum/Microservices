package com.michibaum.authentication_library.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import reactor.core.publisher.Mono

interface SpecificAuthenticationManager {

    @Throws(AuthenticationException::class)
    fun authenticate(authentication: Authentication): Mono<Authentication>

    fun supports(authentication: Class<*>): Boolean

}