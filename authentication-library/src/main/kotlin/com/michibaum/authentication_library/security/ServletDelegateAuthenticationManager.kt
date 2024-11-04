package com.michibaum.authentication_library.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication

class ServletDelegateAuthenticationManager(val authenticationManagers: List<SpecificAuthenticationManager>): AuthenticationManager {
    override fun authenticate(authentication: Authentication?): Authentication {
        if (authentication == null) {
            throw Exception("Empty authentication")
        }

        for(auth in authenticationManagers){
            if(auth.supports(authentication.javaClass)){
                return auth.authenticate(authentication).block() ?: throw Exception("Empty authentication")
            }
        }

        throw NoAuthenticationManagerException("No authentication Manager found for ${authentication::class}")
    }
}