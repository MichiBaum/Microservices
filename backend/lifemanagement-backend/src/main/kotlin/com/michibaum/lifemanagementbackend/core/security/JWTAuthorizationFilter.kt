package com.michibaum.lifemanagementbackend.core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JWTAuthorizationFilter(
    authManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val lastLoginUpdater: LastLoginUpdater
) : BasicAuthenticationFilter(authManager) {

    private val ownLogger = KotlinLogging.logger {}

    @Value("\${application.version}") private val applicationVersion: String = ""

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
        if (token.isBlank()) return null
        val decodedJWT = parseToken(token) ?: return null

        if (!tokenIsValid(decodedJWT)) return null

        val userDetails = userDetailsService.loadUserByUsername(decodedJWT.subject) ?: return null

        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }

    private fun parseToken(token: String): DecodedJWT? =
        try {
            JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET))
                .build()
                .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
        }catch (ex: Exception){
            this.ownLogger.error("Parse token exception: " + ex.message, ex)
            null
        }

    private fun tokenIsValid(jwt: DecodedJWT): Boolean {
        val username = jwt.subject.orEmpty()
        val backendVersion = jwt.claims["backend_version"] ?: return false
        val expiresAt = jwt.expiresAt ?: return false

        if (username.isBlank()) return false
        if (Date().time > expiresAt.time) return false
        if (backendVersion.asString() != applicationVersion) return false

        return true
    }
}
