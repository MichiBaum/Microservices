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
    @field:NotNull(message = "log.validation.id.notNull")
    val id: Long = 0,

    @field:NotNull(message = "log.validation.date.notNull")
    val date: Long = 0,

    @field:NotNull(message = "log.validation.formattedMessage.notNull")
    val formattedMessage: String = "",

    @field:NotNull(message = "log.validation.loggerName.notNull")
    val loggerName: String = "",

    @field:NotNull(message = "log.validation.level.notNull")
    val level: String = "",

    @field:NotNull(message = "log.validation.threadName.notNull")
    val threadName: String = "",

    @field:NotNull(message = "log.validation.arg0.notNull")
    val arg0: String = "",

    @field:NotNull(message = "log.validation.arg1.notNull")
    val arg1: String = "",

    @field:NotNull(message = "log.validation.arg2.notNull")
    val arg2: String = "",

    @field:NotNull(message = "log.validation.arg3.notNull")
    val arg3: String = "",

    @field:NotNull(message = "log.validation.callerFilename.notNull")
    val callerFilename: String = "",

    @field:NotNull(message = "log.validation.callerClass.notNull")
    val callerClass: String = "",

    @field:NotNull(message = "log.validation.callerMethod.notNull")
    val callerMethod: String = "",

    @field:NotNull(message = "log.validation.callerLine.notNull")
    val callerLine: String = "",

    @field:NotNull(message = "log.validation.seen.notNull")
    val seen: Boolean = false
)
