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
import org.springframework.security.web.authentication.*
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class ServletAuthenticationFilter(val authenticationManager: AuthenticationManager, val converters: List<AuthenticationConverter>): OncePerRequestFilter() {

    private val securityContextHolderStrategy: SecurityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy()

    private val successHandler: AuthenticationSuccessHandler = SavedRequestAwareAuthenticationSuccessHandler()

    private val failureHandler: AuthenticationFailureHandler = AuthenticationEntryPointFailureHandler(
        HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
    )

    private val securityContextRepository: SecurityContextRepository = RequestAttributeSecurityContextRepository()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {

            val authenticationResult: Authentication? = converters.map { converter: AuthenticationConverter -> converter.convert(request) }
                .filterNotNull()
                .map { authentication -> authenticationManager.authenticate(authentication) }
                .filterNotNull()
                .firstOrNull()

            if (authenticationResult == null) {
                filterChain.doFilter(request, response)
                return
            }
            val session = request.getSession(false)
            if (session != null) {
                request.changeSessionId()
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
        this.securityContextHolderStrategy.setContext(context)
        this.securityContextRepository.saveContext(context, request, response)
        this.successHandler.onAuthenticationSuccess(request, response, chain, authentication)
    }

}