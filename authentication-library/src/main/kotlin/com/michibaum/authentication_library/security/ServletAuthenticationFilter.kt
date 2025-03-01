package com.michibaum.authentication_library.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolderStrategy
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class ServletAuthenticationFilter(private val authenticationManager: AuthenticationManager, private val converters: List<AuthenticationConverter>): OncePerRequestFilter() {

    private val securityContextHolderStrategy: SecurityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy()

    private val failureHandler: AuthenticationFailureHandler = AuthenticationEntryPointFailureHandler(
        HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
    )

    // TODO is this correct??
    private val securityContextRepository: SecurityContextRepository = RequestAttributeSecurityContextRepository()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {

            val authenticationResult: Authentication? =
                converters.mapNotNull { converter: AuthenticationConverter -> converter.convert(request) }
                    .firstNotNullOfOrNull { authentication -> authenticationManager.authenticate(authentication) }

            if (authenticationResult == null) {
                filterChain.doFilter(request, response)
                return
            }

            successfulAuthentication(request, response, filterChain, authenticationResult)
        } catch (ex: AuthenticationException) {
            unsuccessfulAuthentication(request, response, ex)
        }
    }


    @Throws(IOException::class, ServletException::class)
    private fun unsuccessfulAuthentication(
        request: HttpServletRequest, response: HttpServletResponse,
        failed: AuthenticationException,
    ) {
        this.securityContextHolderStrategy.clearContext()
        this.failureHandler.onAuthenticationFailure(request, response, failed)
    }

    @Throws(IOException::class, ServletException::class)
    private fun successfulAuthentication(
        request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain,
        authentication: Authentication,
    ) {
        val context: SecurityContext = this.securityContextHolderStrategy.createEmptyContext()
        context.authentication = authentication
        this.securityContextHolderStrategy.context = context
        this.securityContextRepository.saveContext(context, request, response)
        chain.doFilter(request, response)
    }

}