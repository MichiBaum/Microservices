package com.michibaum.authentication_library.security.netty

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono

class DelegateAuthenticationManager(val authenticationManagers: List<SpecificAuthenticationManager>): ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        if (authentication == null) {
            return Mono.empty()
        }

        for(auth in authenticationManagers){
            if(auth.supports(authentication.javaClass)){
                return auth.authenticate(authentication)
            }
        }

        throw NoAuthenticationManagerException("No authentication Manager found for ${authentication::class}")

    }




}