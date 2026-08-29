package com.michibaum.vpn_service.app.wireguard

import com.michibaum.authentication_library.anyOf
import com.michibaum.permission_library.Permissions
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class WireguardController(
    private val wireguardService: WireguardService
) {

    @PostMapping(value = ["/api/wireguard"])
    @ResponseStatus(HttpStatus.CREATED)
    fun createDeployment(
        authentication: Authentication?
    ): WireguardDeploymentDto {
        val requestedUser = resolveRequestedUser(authentication)
        return wireguardService.createDeployment(requestedUser)
    }

    @GetMapping("/api/wireguard")
    fun getDeployment(
        authentication: Authentication?
    ): WireguardDeploymentDto? {
        val requestedUser = resolveRequestedUser(authentication)
        return wireguardService.getDeployment(requestedUser)
    }

    @GetMapping("/api/wireguard/config", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getPeerConfig(
        authentication: Authentication?,
        @RequestParam(required = false) username: String?
    ): String {
        val requestedUser = resolveRequestedUser(authentication)
        return wireguardService.getPeerConfig(requestedUser)
    }

    @DeleteMapping("/api/wireguard")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteDeployment(
        authentication: Authentication?
    ) {
        val requestedUser = resolveRequestedUser(authentication)
        wireguardService.deleteDeployment(requestedUser)
    }

    private fun resolveRequestedUser(authentication: Authentication?): String {
        val authUser = authentication?.name?.takeIf { it.isNotBlank() && it != "anonymous" && it != "anonymousUser" }
        ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User not authenticated")

        val hasAllUsers = authentication.anyOf(Permissions.VPN_SERVICE_ALL_USERS)
        val hasOwnUser = authentication.anyOf(Permissions.VPN_SERVICE_OWN_USER)

        if (!hasAllUsers && !hasOwnUser) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied")
        }

        return authUser
    }

}
