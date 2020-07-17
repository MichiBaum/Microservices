package com.michibaum.lifemanagementbackend.logs.dtos

data class LogFilter(
    val level: List<String> = emptyList(),
    val seen: Boolean = false
)
