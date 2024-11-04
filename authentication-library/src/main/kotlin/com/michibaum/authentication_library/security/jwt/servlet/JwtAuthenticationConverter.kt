package com.michibaum.authentication_library.security.jwt.servlet

import com.michibaum.authentication_library.JwsWrapper
import com.michibaum.authentication_library.security.jwt.JwtAuthentication
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

        val token = getToken(request) ?: return null
        return JwtAuthentication(token, createUserDetails(token))
    }

    fun getToken(request: HttpServletRequest): String? {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if(header != null && header.startsWith("Bearer ")) {
            return header.substring("Bearer ".length)
        }

        val cookie = request.cookies.firstOrNull { cookie -> cookie.name == "jwt" }?.value
        if(!cookie.isNullOrBlank()) {
            return cookie
        }
        return null
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