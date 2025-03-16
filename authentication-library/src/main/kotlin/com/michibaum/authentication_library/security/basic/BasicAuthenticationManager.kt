package com.michibaum.authentication_library.security.basic

import com.michibaum.authentication_library.security.SpecificAuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication

class BasicAuthenticationManager(private val credentialsValidator: CredentialsValidator):
    SpecificAuthenticationManager {

    override fun authenticate(authentication: Authentication): Authentication? {

        if (authentication !is BasicAuthentication) {
            return null
        }

        val valid = credentialsValidator.validate(authentication)
        if (!valid) {
            authentication.isAuthenticated = false
            throw BadCredentialsException("Invalid credentials")
        }
        authentication.isAuthenticated = true

        return authentication
    }

    override fun supports(authentication: Class<*>): Boolean {
        return BasicAuthentication::class.java.isAssignableFrom(authentication)
    }

}