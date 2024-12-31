package com.michibaum.authentication_library.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

class ServletDelegateAuthenticationManager(private val authenticationManagers: List<SpecificAuthenticationManager>): AuthenticationManager {
    override fun authenticate(authentication: Authentication?): Authentication {
        if (authentication == null) {
            throw Exception("Empty authentication")
        }

        val auths = mutableListOf<Authentication>()
        for(authManager in authenticationManagers){
            if(authManager.supports(authentication.javaClass)){
                val auth = authManager.authenticate(authentication) ?: continue
                auths.add(auth)
            }
        }

        val authenticated = auths.filter { it.isAuthenticated }

        if(authenticated.size > 1 || authenticated.isEmpty()) {
            throw BadCredentialsException("More than one, or none authentication was authenticated.")
        }

        SecurityContextHolder.getContext().authentication = authenticated[0]
        return authenticated[0]
    }
}