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
    @field:NotNull(message = "validation.log.id.notNull")
    val id: Long = 0,

    @field:NotNull(message = "validation.log.date.notNull")
    val date: Long = 0,

    @field:NotNull(message = "validation.log.formattedMessage.notNull")
    val formattedMessage: String = "",

    @field:NotNull(message = "validation.log.loggerName.notNull")
    val loggerName: String = "",

    @field:NotNull(message = "validation.log.level.notNull")
    val level: String = "",

    @field:NotNull(message = "validation.log.threadName.notNull")
    val threadName: String = "",

    @field:NotNull(message = "validation.log.arg0.notNull")
    val arg0: String = "",

    @field:NotNull(message = "validation.log.arg1.notNull")
    val arg1: String = "",

    @field:NotNull(message = "validation.log.arg2.notNull")
    val arg2: String = "",

    @field:NotNull(message = "validation.log.arg3.notNull")
    val arg3: String = "",

    @field:NotNull(message = "validation.log.callerFilename.notNull")
    val callerFilename: String = "",

    @field:NotNull(message = "validation.log.callerClass.notNull")
    val callerClass: String = "",

    @field:NotNull(message = "validation.log.callerMethod.notNull")
    val callerMethod: String = "",

    @field:NotNull(message = "validation.log.callerLine.notNull")
    val callerLine: String = "",

    @field:NotNull(message = "validation.log.seen.notNull")
    val seen: Boolean = false
)
