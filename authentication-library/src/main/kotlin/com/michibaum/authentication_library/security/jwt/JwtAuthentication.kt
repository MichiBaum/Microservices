package com.michibaum.authentication_library.security.jwt

import com.michibaum.authentication_library.JwsWrapper
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails

class JwtAuthentication(val token: String, val principal: UserDetails): AbstractAuthenticationToken(principal.authorities) {

    private val jwsWrapper = JwsWrapper(token)

    override fun getCredentials(): Any {
        return principal
    }

    override fun getPrincipal(): Any {
        return principal
    }

    fun getUsername() = jwsWrapper.getUsername()

    fun getUserId() = jwsWrapper.getUserId()

    override fun toString(): String {
        return super.toString()
    }

}