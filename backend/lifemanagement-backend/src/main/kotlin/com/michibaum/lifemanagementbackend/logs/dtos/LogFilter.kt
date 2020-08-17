package com.michibaum.lifemanagementbackend.logs.dtos

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Log Filter DTO")
data class LogFilter(
    @field:Schema(required = true, example = "{ERROR,INFO}", description = "The log levels as Array", defaultValue = "{}")
    val level: List<String> = emptyList(),

    @field:Schema(required = true, example = "true", description = "If all seen or all not seen", defaultValue = "false")
    val seen: Boolean = false
)
