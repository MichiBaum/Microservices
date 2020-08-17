package com.michibaum.lifemanagementbackend.logs.controller

import com.michibaum.lifemanagementbackend.core.exceptionHandler.ErrorDetails
import com.michibaum.lifemanagementbackend.logs.domain.LoggingEvent
import com.michibaum.lifemanagementbackend.logs.dtos.LogFilter
import com.michibaum.lifemanagementbackend.logs.dtos.ReturnLogDto
import com.michibaum.lifemanagementbackend.logs.dtos.CreateLogDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Log Endpoints", description = "All api endpoints which handle something with logs")
interface LogRestControllerDocs {

    @SecurityRequirement(name = "Barear Token")
    @Operation(summary = "Returns all logs", description = "Returns all logs as DTO", security = [ SecurityRequirement(name = "SEE_LOGS") ])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "OK: Request successfull completed"),
        ApiResponse(responseCode = "403", description = "Access denied: If an user is not authenticated, token expired, doesnt have the required permissions", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ]),
        ApiResponse(responseCode = "500", description = "Internal Server Error: If something internal broke accidentally", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    fun getLogs(
        logFilter: LogFilter
    ): List<ReturnLogDto>

    @SecurityRequirement(name = "Barear Token")
    @Operation(summary = "Returns all log level", description = "Returns all log level", security = [ SecurityRequirement(name = "SEE_LOGS") ])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "OK: Request successfull completed"),
        ApiResponse(responseCode = "403", description = "Access denied: If an user is not authenticated, token expired, doesnt have the required permissions", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ]),
        ApiResponse(responseCode = "500", description = "Internal Server Error: If something internal broke accidentally", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    fun getLogLevels(): List<String>

    @SecurityRequirement(name = "Barear Token")
    @Operation(summary = "Changes the log level", description = "Resolves the loggingevent by the id in the url and changes the loggingevents log level", security = [ SecurityRequirement(name = "SEE_LOGS") ])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "OK: Request successfull completed"),
        ApiResponse(responseCode = "403", description = "Access denied: If an user is not authenticated, token expired, doesnt have the required permissions", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ]),
        ApiResponse(responseCode = "500", description = "Internal Server Error: If something internal broke accidentally", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    fun setLogSeen(
        loggingEvent: LoggingEvent,
        seen: Boolean
    ): ReturnLogDto

    @SecurityRequirement(name = "Barear Token")
    @Operation(summary = "Create a log", description = "Creates a log level")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "OK: Request successfull completed"),
        ApiResponse(responseCode = "403", description = "Access denied: If an user is not authenticated, token expired, doesnt have the required permissions", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ]),
        ApiResponse(responseCode = "500", description = "Internal Server Error: If something internal broke accidentally", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    fun createLog(
        log: CreateLogDto
    ): ReturnLogDto
}
