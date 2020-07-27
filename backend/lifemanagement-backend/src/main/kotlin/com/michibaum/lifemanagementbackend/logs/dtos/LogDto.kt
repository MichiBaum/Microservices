package com.michibaum.lifemanagementbackend.logs.dtos

import io.swagger.v3.oas.annotations.media.Schema
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
    @field:Schema(example = "34", required = false)
    @field:NotNull(message = "validation.log.id.notNull")
    val id: Long = 0,

    @field:Schema(example = "1595851650180", required = true)
    @field:NotNull(message = "validation.log.date.notNull")
    val date: Long = 0,

    @field:Schema(example = "Exception: You cant be null", required = true, description = "The formatted message from a logger or something")
    @field:NotNull(message = "validation.log.formattedMessage.notNull")
    val formattedMessage: String = "",

    @field:Schema(example = "CostomExceptionLogger", required = true, description = "The logger who logged the exception")
    @field:NotNull(message = "validation.log.loggerName.notNull")
    val loggerName: String = "",

    @field:Schema(example = "INFO", required = true, description = "The level of the exception")
    @field:NotNull(message = "validation.log.level.notNull")
    val level: String = "",

    @field:Schema(required = false)
    @field:NotNull(message = "validation.log.threadName.notNull")
    val threadName: String = "",

    @field:Schema(required = false)
    @field:NotNull(message = "validation.log.arg0.notNull")
    val arg0: String = "",

    @field:Schema(required = false)
    @field:NotNull(message = "validation.log.arg1.notNull")
    val arg1: String = "",

    @field:Schema(required = false)
    @field:NotNull(message = "validation.log.arg2.notNull")
    val arg2: String = "",

    @field:Schema(required = false)
    @field:NotNull(message = "validation.log.arg3.notNull")
    val arg3: String = "",

    @field:Schema(example = "NormalServiceClass.kt", required = true, description = "The filname where the exception happened or the logger logged it")
    @field:NotNull(message = "validation.log.callerFilename.notNull")
    val callerFilename: String = "",

    @field:Schema(example = "NormalServiceClass", required = false, description = "The class where the exception happened or the logger logged it")
    @field:NotNull(message = "validation.log.callerClass.notNull")
    val callerClass: String = "",

    @field:Schema(example = "parseSomethingToElse()", required = false, description = "The method where the exception happened or the logger logged it")
    @field:NotNull(message = "validation.log.callerMethod.notNull")
    val callerMethod: String = "",

    @field:Schema(required = false)
    @field:NotNull(message = "validation.log.callerLine.notNull")
    val callerLine: String = "",

    @field:Schema(example = "false", required = true, description = "If exception already seen true else false")
    @field:NotNull(message = "validation.log.seen.notNull")
    val seen: Boolean = false
)
