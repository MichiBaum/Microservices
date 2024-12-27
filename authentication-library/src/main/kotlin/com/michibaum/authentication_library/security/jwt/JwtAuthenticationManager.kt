package com.michibaum.authentication_library.security.jwt

import com.michibaum.authentication_library.JwsValidationFailed
import com.michibaum.authentication_library.JwsValidationSuccess
import com.michibaum.authentication_library.security.SpecificAuthenticationManager
import org.springframework.security.core.Authentication

class JwtAuthenticationManager(private val jwtService: JwsValidator): SpecificAuthenticationManager {

    override fun authenticate(authentication: Authentication): Authentication? {

        if(authentication !is JwtAuthentication){
            return null
        }
        val result = jwtService.validate(authentication.token)
        when (result) {
            is JwsValidationSuccess -> authentication.apply { isAuthenticated = true }
            is JwsValidationFailed -> authentication.apply { isAuthenticated = false }
        }

        return authentication
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthentication::class.java.isAssignableFrom(authentication)
    }
}
