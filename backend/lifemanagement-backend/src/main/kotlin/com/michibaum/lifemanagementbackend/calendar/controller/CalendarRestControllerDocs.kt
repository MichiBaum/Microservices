package com.michibaum.lifemanagementbackend.calendar.controller

import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.calendar.dtos.ReturnCalendarDto
import com.michibaum.lifemanagementbackend.core.exceptionHandler.ErrorDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Calendar Endpoints", description = "All api endpoints which handle something with calendars")
interface CalendarRestControllerDocs {

    @SecurityRequirement(name = "Barear Token")
    @Operation(summary = "Returns users calendars", description = "Returns all Calendars the user has")
    @ApiResponses(value = [
        ApiResponse( responseCode = "200", description = ""),
        ApiResponse( responseCode = "403", description = "Access denied", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    fun myCalendars(
        currentUser: User
    ): List<ReturnCalendarDto>

    @SecurityRequirement(name = "Barear Token")
    @Operation(summary = "Returns all calendars", description = "Returns all calendars", security = [ SecurityRequirement(name = "ADMIN") ])
    @ApiResponses(value = [
        ApiResponse( responseCode = "200", description = ""),
        ApiResponse( responseCode = "403", description = "Access denied", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    fun allCalendars(): List<ReturnCalendarDto>

    // TODO create Calendar endpoint

}
