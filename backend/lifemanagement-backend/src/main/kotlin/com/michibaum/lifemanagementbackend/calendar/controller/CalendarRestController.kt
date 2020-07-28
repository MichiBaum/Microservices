package com.michibaum.lifemanagementbackend.calendar.controller

import com.michibaum.lifemanagementbackend.core.argumentresolver.ArgumentResolver
import com.michibaum.lifemanagementbackend.calendar.converter.toDto
import com.michibaum.lifemanagementbackend.calendar.domain.Calendar
import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.calendar.dtos.ReturnCalendarDto
import com.michibaum.lifemanagementbackend.calendar.service.CalendarService
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class CalendarRestController(
    private val calendarService: CalendarService
): CalendarRestControllerDocs {

    @RequestMapping(value = ["/lifemanagement/api/calendars/own"], method = [RequestMethod.GET], produces = ["application/json" ])
    override fun myCalendars(

        @Parameter(description = "The current user, autoresolved through @ArgumentResolver", hidden = true)
        @ArgumentResolver
        currentUser: User

    ): List<ReturnCalendarDto> =
        calendarService.findByUser(currentUser)
            .map(Calendar::toDto)

    @PreAuthorize("hasAuthority('ADMIN')") //TODO change permission to something else
    @RequestMapping(value = ["/lifemanagement/api/calendars"], method = [RequestMethod.GET], produces = ["application/json" ])
    override fun allCalendars(): List<ReturnCalendarDto> =
        calendarService.findAll()
            .map(Calendar::toDto)

    // TODO create Calendar endpoint

}