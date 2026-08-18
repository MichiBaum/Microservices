package com.michibaum.admin_service.app.kubernetes.dto

data class ServiceDto(
    val name: String,
    val namespace: String,
    val type: String,
    val clusterIp: String?,
    val ports: List<ServicePortDto>,
    val selector: Map<String, String>
)

data class ServicePortDto(
    val name: String?,
    val port: Int,
    val targetPort: String?,
    val protocol: String
)
