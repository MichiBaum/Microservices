package com.michibaum.lifemanagementbackend.logs.dtos

data class ReturnLogDto(
    val id: Long = 0,
    val date: Long = 0,
    val formattedMessage: String = "",
    val loggerName: String = "",
    val level: String = "",
    val threadName: String = "",
    val arg0: String = "",
    val arg1: String = "",
    val arg2: String = "",
    val arg3: String = "",
    val callerFilename: String = "",
    val callerClass: String = "",
    val callerMethod: String = "",
    val callerLine: String = "",
    val seen: Boolean = false
)

data class UpdateLogDto(
    val id: Long = 0,
    val date: Long = 0,
    val formattedMessage: String = "",
    val loggerName: String = "",
    val level: String = "",
    val threadName: String = "",
    val arg0: String = "",
    val arg1: String = "",
    val arg2: String = "",
    val arg3: String = "",
    val callerFilename: String = "",
    val callerClass: String = "",
    val callerMethod: String = "",
    val callerLine: String = "",
    val seen: Boolean = false
)
