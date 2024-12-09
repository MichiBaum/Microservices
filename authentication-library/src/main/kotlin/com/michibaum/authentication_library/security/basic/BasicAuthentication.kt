package com.michibaum.authentication_library.security.basic

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails

class BasicAuthentication(private val userDetails: UserDetails) : AbstractAuthenticationToken(userDetails.authorities) {

    override fun getCredentials(): String {
        return userDetails.password
    }

    override fun getPrincipal(): UserDetails {
        return userDetails
    }

    fun getUsername(): String =
        userDetails.username

    fun getPassword(): String =
        userDetails.password

}