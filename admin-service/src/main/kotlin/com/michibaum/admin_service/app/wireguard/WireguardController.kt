package com.michibaum.admin_service.app.wireguard

import com.michibaum.admin_service.app.kubernetes.dto.DeploymentDto
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
        val requestedUser = authentication?.name?.takeIf { it.isNotBlank() && it != "anonymous" && it != "anonymousUser" }
            ?: username?.takeIf { it.isNotBlank() && it != "anonymous" && it != "anonymousUser" }
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is not set")
        return wireguardService.createWireguardDeployment(requestedUser, namespace)
    }

    @GetMapping("/config", "", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getPeerConfig(
        authentication: Authentication?,
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) namespace: String?
    ): String {
        val requestedUser = authentication?.name?.takeIf { it.isNotBlank() && it != "anonymous" && it != "anonymousUser" }
            ?: username?.takeIf { it.isNotBlank() && it != "anonymous" && it != "anonymousUser" }
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is not set")
        return wireguardService.getPeerConfig(requestedUser, namespace)
    }

}
