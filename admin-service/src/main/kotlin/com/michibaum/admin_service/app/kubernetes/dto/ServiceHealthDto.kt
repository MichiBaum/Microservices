package com.michibaum.admin_service.app.kubernetes.dto

data class ServiceHealthDto(
    val status: String,
    val details: Map<String, Any> = emptyMap()
)
