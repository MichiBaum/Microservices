package com.michibaum.lifemanagementbackend.logs.dtos

import io.swagger.v3.oas.annotations.media.Schema

data class LogFilter(
    @field:Schema(required = true)
    val level: List<String> = emptyList(),

    @field:Schema(required = true)
    val seen: Boolean = false
)
