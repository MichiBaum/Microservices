package com.michibaum.lifemanagementbackend.logs.controller

import com.michibaum.lifemanagementbackend.core.exceptionHandler.ErrorDetails
import com.michibaum.lifemanagementbackend.logs.converter.toDto
import com.michibaum.lifemanagementbackend.logs.converter.toLoggingEvent
import com.michibaum.lifemanagementbackend.logs.domain.LoggingEvent
import com.michibaum.lifemanagementbackend.logs.dtos.LogFilter
import com.michibaum.lifemanagementbackend.logs.dtos.ReturnLogDto
import com.michibaum.lifemanagementbackend.logs.dtos.CreateLogDto
import com.michibaum.lifemanagementbackend.logs.service.LogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@Tag(name = "Log Endpoints", description = "")
class LogRestController(
    private val logService: LogService
) {

    @Operation(summary = "Returns all logs", description = "Returns all logs as DTO", security = [ SecurityRequirement(name = "SEE_LOGS") ])
    @ApiResponses(value = [
        ApiResponse( responseCode = "200", description = ""),
        ApiResponse( responseCode = "403", description = "Access denied", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    @PreAuthorize("hasAuthority('SEE_LOGS')")
    @RequestMapping(value = ["/lifemanagement/api/logs"], method = [RequestMethod.GET], produces = ["application/json" ], consumes = ["application/json" ])
    fun getLogs(
        @Parameter(description = "") logFilter: LogFilter
    ): List<ReturnLogDto> =
        logService.findByFilter(logFilter)
            .map(LoggingEvent::toDto)

    @Operation(summary = "Returns all log level", description = "Returns all log level", security = [ SecurityRequirement(name = "SEE_LOGS") ])
    @ApiResponses(value = [
        ApiResponse( responseCode = "200", description = ""),
        ApiResponse( responseCode = "403", description = "Access denied", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    @PreAuthorize("hasAuthority('SEE_LOGS')")
    @RequestMapping(value = ["/lifemanagement/api/logs/levels"], method = [RequestMethod.GET], produces = ["application/json" ], consumes = ["application/json" ])
    fun getLogLevels(): List<String> =
        logService.findAll()
            .map(LoggingEvent::levelString)
            .distinct()

    @Operation(summary = "Changes the log level", description = "Resolves the loggingevent by the id in the url and changes the loggingevents log level", security = [ SecurityRequirement(name = "SEE_LOGS") ])
    @ApiResponses(value = [
        ApiResponse( responseCode = "200", description = ""),
        ApiResponse( responseCode = "403", description = "Access denied", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    @PreAuthorize("hasAuthority('SEE_LOGS')")
    @RequestMapping(value = ["/lifemanagement/api/logs/{loggingEvent}/seen"], method = [RequestMethod.POST], produces = ["application/json" ], consumes = ["application/json" ])
    fun setLogSeen(
        @Parameter(description = "The logging event. Is resolved by the url variable", hidden = true) @PathVariable loggingEvent: LoggingEvent,
        @Parameter(description = "Boolean if seen or not") @RequestBody seen: Boolean
    ) =
        logService.changeSeen(loggingEvent, seen)
            .let(logService::save)
            .toDto()

    @Operation(summary = "Create a log", description = "Creates a log level")
    @ApiResponses(value = [
        ApiResponse( responseCode = "200", description = ""),
        ApiResponse( responseCode = "403", description = "Access denied", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    @RequestMapping(value = ["/lifemanagement/api/logs"], method = [RequestMethod.POST], produces = ["application/json" ], consumes = ["application/json" ])
    fun createLog(
        @Parameter(description = "The Data to create the log") @Valid @RequestBody log: CreateLogDto
    ) =
        log.toLoggingEvent()
            .let(logService::save)
            .toDto()
}
