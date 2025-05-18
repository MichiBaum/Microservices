package com.michibaum.authentication_library.security.jwt

import com.michibaum.authentication_library.JwsValidationFailed
import com.michibaum.authentication_library.JwsValidationSuccess
import com.michibaum.authentication_library.security.SpecificAuthenticationManager
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import org.springframework.security.core.Authentication


class JwtAuthenticationManager(private val jwtService: JwsValidator, private val observationRegistry: ObservationRegistry): SpecificAuthenticationManager {

    override fun authenticate(authentication: Authentication): Authentication? {

        if(authentication !is JwtAuthentication){
            return null
        }


        val observation = Observation.start("jwt-authentication-validation", this.observationRegistry)
        val result = jwtService.validate(authentication.token)
        when (result) {
            is JwsValidationSuccess -> {
                authentication.apply { isAuthenticated = true }
                observation.lowCardinalityKeyValue("jwt-authentication-validation-result", "success")
                observation.highCardinalityKeyValue("authenticated-user-id", authentication.getUserId())
            }
            is JwsValidationFailed -> {
                authentication.apply { isAuthenticated = false }
                observation.lowCardinalityKeyValue("jwt-authentication-validation-result", "failed")
                throw InvalidJwtException()
            }
        }
        observation.stop()

        return authentication
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthentication::class.java.isAssignableFrom(authentication)
    }
}
