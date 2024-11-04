package com.michibaum.authentication_library.security.basic.servlet

import com.michibaum.authentication_library.security.basic.BasicAuthentication
import com.michibaum.permission_library.Permissions
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.AuthenticationConverter
import java.util.*

class BasicAuthenticationConverter: AuthenticationConverter {
    override fun convert(request: HttpServletRequest?): Authentication? {
        if (request == null) {
            return null
        }

        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return null
        }

        val token = authHeader.substring("Basic ".length)
        return BasicAuthentication(createUserDetails(token))
    }

    private fun createUserDetails(token: String): UserDetails {
        val decoded = Base64.getDecoder().decode(token)
        val usernameAndPassword = String(decoded).split(":")
        return User.builder()
            .username(usernameAndPassword[0])
            .authorities(SimpleGrantedAuthority(Permissions.ADMIN_SERVICE.name))
            .password(usernameAndPassword[1])
            .build()
    }

}