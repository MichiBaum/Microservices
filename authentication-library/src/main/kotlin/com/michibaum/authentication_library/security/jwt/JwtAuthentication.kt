package com.michibaum.authentication_library.security.jwt

import com.michibaum.authentication_library.JwsWrapper
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails

class JwtAuthentication(val token: String, private val userDetails: UserDetails): AbstractAuthenticationToken(userDetails.authorities) {

    private val jwsWrapper = JwsWrapper(token)

    override fun getCredentials(): String {
        return userDetails.password
    }

    override fun getPrincipal(): UserDetails {
        return userDetails
    }

    fun getUsername(): String =
        jwsWrapper.getUsername()

    fun getUserId(): String =
        jwsWrapper.getUserId()

}