package com.michibaum.admin_service.app.kubernetes.dto

data class CreatePodRequestDto(
    val name: String,
    val image: String,
    val containerPort: Int = 80,
    val environmentVariables: Map<String, String> = emptyMap(),
    val labels: Map<String, String> = emptyMap()
)
