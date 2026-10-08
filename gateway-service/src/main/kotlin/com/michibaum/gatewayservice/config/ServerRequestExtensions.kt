package com.michibaum.gatewayservice.config

import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.permission_library.Permissions
import org.springframework.cloud.gateway.server.mvc.common.MvcUtils.GATEWAY_REQUEST_URL_ATTR
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.net.URI

fun authenticatedHttp(
    redirect: URI,
    authFilter: ServletAuthenticationFilter,
    vararg requiredPermissions: Permissions
): HandlerFunction<ServerResponse> = HandlerFunction { request ->
    request.authenticate(redirect, authFilter, *requiredPermissions)
}

fun ServerRequest.authenticate(
    redirect: URI,
    authFilter: ServletAuthenticationFilter,
    vararg requiredPermissions: Permissions
): ServerResponse {
    val authentication = authFilter.getAuthentication(servletRequest())
    return if (!hasRequiredPermissions(authentication, requiredPermissions)) {
        ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
    } else {
        val requestWithRedirect = ServerRequest.from(this)
            .attribute(GATEWAY_REQUEST_URL_ATTR, redirect)
            .build()
        http().handle(requestWithRedirect)
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