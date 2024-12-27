package com.michibaum.authentication_library.security.basic

import com.michibaum.authentication_library.security.SpecificAuthenticationManager
import org.springframework.security.core.Authentication

class BasicAuthenticationManager(private val credentialsValidator: CredentialsValidator):
    SpecificAuthenticationManager {

    override fun authenticate(authentication: Authentication): Authentication? {

        if (authentication !is BasicAuthentication) {
            return null
        }

        val valid = credentialsValidator.validate(authentication)
        if (!valid) {
            return null
        }
        authentication.isAuthenticated = true

        return authentication
    }

    override fun supports(authentication: Class<*>): Boolean {
        return BasicAuthentication::class.java.isAssignableFrom(authentication)
    }

}