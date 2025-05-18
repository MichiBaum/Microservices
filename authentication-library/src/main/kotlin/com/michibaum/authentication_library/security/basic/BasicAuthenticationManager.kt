package com.michibaum.authentication_library.security.basic

import com.michibaum.authentication_library.security.SpecificAuthenticationManager
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication

class BasicAuthenticationManager(private val credentialsValidator: CredentialsValidator, private val observationRegistry: ObservationRegistry):
    SpecificAuthenticationManager {

    override fun authenticate(authentication: Authentication): Authentication? {

        if (authentication !is BasicAuthentication) {
            return null
        }

        val observation = Observation.start("basic-authentication-validation", this.observationRegistry)
        val valid = credentialsValidator.validate(authentication)
        if (!valid) {
            authentication.isAuthenticated = false
            observation.lowCardinalityKeyValue("basic-authentication-validation-result", "failed")
            throw BadCredentialsException("Invalid credentials")
        }
        authentication.isAuthenticated = true
        observation.lowCardinalityKeyValue("basic-authentication-validation-result", "success")
        observation.highCardinalityKeyValue("authenticated-user-name", authentication.getUsername())
        observation.stop()

        return authentication
    }

    override fun supports(authentication: Class<*>): Boolean {
        return BasicAuthentication::class.java.isAssignableFrom(authentication)
    }

}