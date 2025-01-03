package com.michibaum.authentication_library.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException

interface SpecificAuthenticationManager {

    @Throws(AuthenticationException::class)
    fun authenticate(authentication: Authentication): Authentication?

    fun supports(authentication: Class<*>): Boolean

}