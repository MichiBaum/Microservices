package com.michibaum.lifemanagementbackend.dtos


class LogFilter (
    val level: List<String> = emptyList(),
    val seen: Boolean = false
)
