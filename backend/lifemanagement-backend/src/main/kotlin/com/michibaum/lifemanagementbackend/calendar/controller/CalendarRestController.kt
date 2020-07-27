package com.michibaum.lifemanagementbackend.calendar.controller

import com.michibaum.lifemanagementbackend.core.argumentresolver.ArgumentResolver
import com.michibaum.lifemanagementbackend.calendar.converter.toDto
import com.michibaum.lifemanagementbackend.calendar.domain.Calendar
import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.calendar.dtos.ReturnCalendarDto
import com.michibaum.lifemanagementbackend.calendar.service.CalendarService
import com.michibaum.lifemanagementbackend.core.exceptionHandler.ErrorDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Calendar Endpoints", description = "")
class CalendarRestController(
    private val calendarService: CalendarService
) {

    @Operation(summary = "Returns users calendars", description = "Returns all Calendars the user has")
    @ApiResponses(value = [
        ApiResponse( responseCode = "200", description = ""),
        ApiResponse( responseCode = "403", description = "Access denied", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    @RequestMapping(value = ["/lifemanagement/api/calendars/own"], method = [RequestMethod.GET], produces = ["application/json" ], consumes = ["application/json" ])
    fun myCalendars(
        @Parameter(description = "The current user, autoresolved through @ArgumentResolver", hidden = true) @ArgumentResolver currentUser: User
    ): List<ReturnCalendarDto> =
        calendarService.findByUser(currentUser)
            .map(Calendar::toDto)

    @Operation(summary = "Returns all calendars", description = "Returns all calendars", security = [ SecurityRequirement(name = "ADMIN") ])
    @ApiResponses(value = [
        ApiResponse( responseCode = "200", description = ""),
        ApiResponse( responseCode = "403", description = "Access denied", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    @PreAuthorize("hasAuthority('ADMIN')") //TODO change permission to something else
    @RequestMapping(value = ["/lifemanagement/api/calendars"], method = [RequestMethod.GET], produces = ["application/json" ], consumes = ["application/json" ])
    fun allCalendars(): List<ReturnCalendarDto> =
        calendarService.findAll()
            .map(Calendar::toDto)

    // TODO create Calendar endpoint

}
