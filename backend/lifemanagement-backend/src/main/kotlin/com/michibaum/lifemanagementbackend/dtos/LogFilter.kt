package com.michibaum.lifemanagementbackend.dtos

data class LogFilter(
    val level: List<String> = emptyList(),
    val seen: Boolean = false
)
