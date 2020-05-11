package com.michibaum.lifemanagementbackend.logs.dtos


class LogFilter (
    val level: List<String> = emptyList(),
    val seen: Boolean = false
)
