package com.michibaum.vpn_service.app.wireguard

data class WireguardDeploymentDto(
    val name: String,
    val creationTimestamp: String?,
    val containers: List<String>,
    val port: Int? = null,
    val nodePort: Int? = null
)