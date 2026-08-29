package com.michibaum.vpn_service.app.kubernetes.dto

data class DeploymentDto(
    val name: String,
    val namespace: String,
    val replicas: Int?,
    val readyReplicas: Int?,
    val creationTimestamp: String?,
    val containers: List<String>
)
