package com.michibaum.authentication_library.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import reactor.core.publisher.Mono

class ReactiveDelegateAuthenticationManager(private val authenticationManagers: List<SpecificAuthenticationManager>): ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        if (authentication == null) {
            return Mono.empty()
        }

        for(authManager in authenticationManagers){
            if(authManager.supports(authentication.javaClass)){
                val auth = authManager.authenticate(authentication) ?: return Mono.error(Exception("Empty authentication"))
                ReactiveSecurityContextHolder.withAuthentication(auth)
                return Mono.just(authentication)
            }
        }

        throw NoAuthenticationManagerException("No authentication Manager found for ${authentication::class}")
    }




}