package com.michibaum.lifemanagementbackend.loggs.dtos


class LogFilter (
    val level: List<String> = emptyList(),
    val seen: Boolean = false
)
