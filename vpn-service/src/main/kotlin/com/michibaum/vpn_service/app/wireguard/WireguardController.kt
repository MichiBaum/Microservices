package com.michibaum.vpn_service.app.wireguard

import com.michibaum.authentication_library.anyOf
import com.michibaum.permission_library.Permissions
import com.michibaum.vpn_service.app.kubernetes.dto.DeploymentDto
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/wireguard", "/api/k8s/wireguard")
class WireguardController(
    private val wireguardService: WireguardService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createWireguardDeployment(
        authentication: Authentication?,
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) namespace: String?
    ): DeploymentDto {
        val requestedUser = resolveRequestedUser(authentication, username)
        return wireguardService.createWireguardDeployment(requestedUser, namespace)
    }

    @GetMapping("/config", "", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getPeerConfig(
        authentication: Authentication?,
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) namespace: String?
    ): String {
        val requestedUser = resolveRequestedUser(authentication, username)
        return wireguardService.getPeerConfig(requestedUser, namespace)
    }

    private fun resolveRequestedUser(authentication: Authentication?, usernameParam: String?): String {
        val authUser = authentication?.name?.takeIf { it.isNotBlank() && it != "anonymous" && it != "anonymousUser" }
        val targetUser = usernameParam?.takeIf { it.isNotBlank() && it != "anonymous" && it != "anonymousUser" } ?: authUser
        ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is not set")

        if (authentication != null) {
            val isOtherUser = authUser != null && authUser != targetUser
            val hasAllUsers = authentication.anyOf(Permissions.VPN_SERVICE_ALL_USERS)
            val hasOwnUser = authentication.anyOf(Permissions.VPN_SERVICE_OWN_USER)

            if (isOtherUser && !hasAllUsers) {
                throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied")
            }
            if (!hasAllUsers && !hasOwnUser) {
                throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied")
            }
        }

        return targetUser
    }

}
