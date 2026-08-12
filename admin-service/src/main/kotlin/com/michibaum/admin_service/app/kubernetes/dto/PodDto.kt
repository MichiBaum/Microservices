package com.michibaum.admin_service.app.kubernetes.dto

data class PodDto(
    val name: String,
    val namespace: String,
    val status: String,
    val podIp: String?,
    val nodeName: String?,
    val creationTimestamp: String?,
    val containers: List<String>
)
