package com.michibaum.lifemanagementbackend.logs.dtos

import javax.validation.constraints.NotNull

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

data class CreateLogDto(
    @NotNull(message = "log.validation.id.notNull")
    val id: Long = 0,

    @NotNull(message = "log.validation.date.notNull")
    val date: Long = 0,

    @NotNull(message = "log.validation.formattedMessage.notNull")
    val formattedMessage: String = "",

    @NotNull(message = "log.validation.loggerName.notNull")
    val loggerName: String = "",

    @NotNull(message = "log.validation.level.notNull")
    val level: String = "",

    @NotNull(message = "log.validation.threadName.notNull")
    val threadName: String = "",

    @NotNull(message = "log.validation.arg0.notNull")
    val arg0: String = "",

    @NotNull(message = "log.validation.arg1.notNull")
    val arg1: String = "",

    @NotNull(message = "log.validation.arg2.notNull")
    val arg2: String = "",

    @NotNull(message = "log.validation.arg3.notNull")
    val arg3: String = "",

    @NotNull(message = "log.validation.callerFilename.notNull")
    val callerFilename: String = "",

    @NotNull(message = "log.validation.callerClass.notNull")
    val callerClass: String = "",

    @NotNull(message = "log.validation.callerMethod.notNull")
    val callerMethod: String = "",

    @NotNull(message = "log.validation.callerLine.notNull")
    val callerLine: String = "",

    @NotNull(message = "log.validation.seen.notNull")
    val seen: Boolean = false
)
