package com.michibaum.authentication_library.security.jwt.servlet

import com.michibaum.authentication_library.JwsWrapper
import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.authentication_library.security.jwt.JwtAuthenticationException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.AuthenticationConverter

class JwtAuthenticationConverter: AuthenticationConverter {
    override fun convert(request: HttpServletRequest?): Authentication? {
        if(request == null){
            return null
        }

        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null
        }

        val token = authHeader.substring("Bearer ".length)
        return JwtAuthentication(token, createUserDetails(token))
    }

    private fun createUserDetails(token: String): UserDetails {
        val username: String = JwsWrapper(token).getUsername()
        return User.builder()
            .username(username)
            .authorities(createAuthorities(token))
            .password("")
            .build()
    }

    private fun createAuthorities(token: String): List<SimpleGrantedAuthority> {
        return JwsWrapper(token).getPermissions().stream()
            .map { permission -> "$permission" }
            .map { permission: String? -> SimpleGrantedAuthority(permission) }
            .toList()
    }

}