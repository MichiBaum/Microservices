package com.michibaum.authentication_library.security.netty.basic

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails

class BasicAuthentication(val principal: UserDetails) : AbstractAuthenticationToken(principal.authorities) {
    override fun getCredentials(): Any {
        return principal
    }

    override fun getPrincipal(): Any {
        return principal
    }

    fun getUsername() =
        principal.username

    fun getPassword() =
        principal.password

    override fun toString(): String {
        return super.toString()
    }

}