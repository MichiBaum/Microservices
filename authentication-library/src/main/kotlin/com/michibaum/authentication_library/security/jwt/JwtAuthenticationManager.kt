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
                val userId = authentication.getUserIdOrNull()?.takeIf { it.isNotBlank() }
                if (userId == null) {
                    authentication.apply { isAuthenticated = false }
                    observation.lowCardinalityKeyValue("jwt-authentication-validation-result", "failed")
                    throw InvalidJwtException("Missing userId claim in JWT")
                }
                authentication.apply { isAuthenticated = true }
                observation.lowCardinalityKeyValue("jwt-authentication-validation-result", "success")
                observation.highCardinalityKeyValue("authenticated-user-id", userId)
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
