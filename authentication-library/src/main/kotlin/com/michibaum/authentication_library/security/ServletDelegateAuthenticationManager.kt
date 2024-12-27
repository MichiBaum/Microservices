package com.michibaum.authentication_library.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

class ServletDelegateAuthenticationManager(private val authenticationManagers: List<SpecificAuthenticationManager>): AuthenticationManager {
    override fun authenticate(authentication: Authentication?): Authentication {
        if (authentication == null) {
            throw Exception("Empty authentication")
        }

        for(authManager in authenticationManagers){
            if(authManager.supports(authentication.javaClass)){
                val auth =  authManager.authenticate(authentication) ?: throw Exception("Empty authentication")
                SecurityContextHolder.getContext().authentication = auth
                return auth
            }
        }

        throw NoAuthenticationManagerException("No authentication Manager found for ${authentication::class}")
    }
}