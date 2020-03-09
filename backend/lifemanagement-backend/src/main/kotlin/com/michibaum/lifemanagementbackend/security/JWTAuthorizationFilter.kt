package com.michibaum.lifemanagementbackend.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JWTAuthorizationFilter(
    authManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val lastLoginUpdater: LastLoginUpdater
) : BasicAuthenticationFilter(authManager) {

    override fun doFilterInternal(req: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header = req.getHeader(SecurityConstants.HEADER_STRING).orEmpty()

        if (header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            val authentication = getAuthentication(req)
            SecurityContextHolder.getContext().authentication = authentication

            lastLoginUpdater.update()
        }

        chain.doFilter(req, response)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token = request.getHeader(SecurityConstants.HEADER_STRING).orEmpty()
        if(token.isBlank()) return null
        val username = parseToken(token).orEmpty()
        if(username.isBlank()) return null
        val userdetails = userDetailsService.loadUserByUsername(username) ?: return null
        return UsernamePasswordAuthenticationToken(userdetails, null, userdetails.authorities)
    }

    private fun parseToken(token: String): String? =
        JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET))
            .build()
            .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
            .subject
}
