package com.michibaum.vpn_service.app.wireguard

data class DeploymentDto(
    val name: String,
    val namespace: String,
    val replicas: Int?,
    val readyReplicas: Int?,
    val creationTimestamp: String?,
    val containers: List<String>
)