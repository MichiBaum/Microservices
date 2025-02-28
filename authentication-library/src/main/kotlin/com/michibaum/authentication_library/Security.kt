package com.michibaum.authentication_library

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

class Security {

    companion object {
        var authentication: Authentication?
        get() = SecurityContextHolder.getContext().authentication
        set(authentication) {
                SecurityContextHolder.getContext().authentication = authentication
            }
    }

}