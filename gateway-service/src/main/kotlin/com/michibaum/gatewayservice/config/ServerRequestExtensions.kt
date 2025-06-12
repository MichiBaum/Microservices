package com.michibaum.gatewayservice.config

import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.permission_library.Permissions
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.net.URI

fun ServerRequest.authenticateWithCircuitBreaker(
    redirect: URI,
    authFilter: ServletAuthenticationFilter,
    circuitBreakerFactory: CircuitBreakerFactory<*, *>,
    serviceName: String,
    vararg requiredPermissions: Permissions
): ServerResponse {
    val authentication = authFilter.getAuthentication(servletRequest())

    if (!hasRequiredPermissions(authentication, requiredPermissions)) {
        return ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
    }

    val circuitBreaker = createCircuitBreaker(serviceName, circuitBreakerFactory)
    return try {
        circuitBreaker.run(
            { http(redirect).handle(this) },
            createCircuitBreakerServiceUnavailableResponse(serviceName)
        )
    } catch (e: Exception) {
        createCircuitBreakerErrorResponse(e)
    }
}

fun ServerRequest.authenticate(redirect: URI, authFilter: ServletAuthenticationFilter, vararg requiredPermissions: Permissions): ServerResponse {
    val authentication = authFilter.getAuthentication(servletRequest())
    return if (authentication == null || !authentication.isAuthenticated || !authentication.authorities.map { it.authority }.containsAll(requiredPermissions.toList().map { it.name })) {
        ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
    } else {
        http(redirect).handle(this)
    }
}

private fun hasRequiredPermissions(
    authentication: Authentication?,
    requiredPermissions: Array<out Permissions>
): Boolean {
    if (authentication == null || !authentication.isAuthenticated) {
        return false
    }

    val userPermissions = authentication.authorities.map { it.authority }
    val requiredPermissionNames = requiredPermissions.map { it.name }
    return userPermissions.containsAll(requiredPermissionNames)
}