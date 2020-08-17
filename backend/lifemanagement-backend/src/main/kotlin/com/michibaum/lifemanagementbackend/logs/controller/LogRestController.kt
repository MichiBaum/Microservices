package com.michibaum.lifemanagementbackend.logs.controller

import com.michibaum.lifemanagementbackend.logs.converter.toDto
import com.michibaum.lifemanagementbackend.logs.converter.toLoggingEvent
import com.michibaum.lifemanagementbackend.logs.domain.LoggingEvent
import com.michibaum.lifemanagementbackend.logs.dtos.LogFilter
import com.michibaum.lifemanagementbackend.logs.dtos.ReturnLogDto
import com.michibaum.lifemanagementbackend.logs.dtos.CreateLogDto
import com.michibaum.lifemanagementbackend.logs.service.LogService
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class LogRestController(
    private val logService: LogService
): LogRestControllerDocs {

    @PreAuthorize("hasAuthority('SEE_LOGS')")
    @RequestMapping(value = ["/lifemanagement/api/logs"], method = [RequestMethod.GET], produces = ["application/json" ])
    override fun getLogs(

        @Parameter(description = "", required = true)
        logFilter: LogFilter

    ): List<ReturnLogDto> =
        logService.findByFilter(logFilter)
            .map(LoggingEvent::toDto)

    @PreAuthorize("hasAuthority('SEE_LOGS')")
    @RequestMapping(value = ["/lifemanagement/api/logs/levels"], method = [RequestMethod.GET], produces = ["application/json" ])
    override fun getLogLevels(): List<String> =
        logService.findAll()
            .map(LoggingEvent::levelString)
            .distinct()

    @PreAuthorize("hasAuthority('SEE_LOGS')")
    @RequestMapping(value = ["/lifemanagement/api/logs/{loggingEvent}/seen"], method = [RequestMethod.POST], produces = ["application/json" ])
    override fun setLogSeen(

        @Parameter(description = "The logging event. Is resolved by the url variable", hidden = true, required = true)
        @PathVariable
        loggingEvent: LoggingEvent,

        @Parameter(description = "Boolean if seen or not")
        @RequestBody
        seen: Boolean

    ): ReturnLogDto =
        logService.changeSeen(loggingEvent, seen)
            .let(logService::save)
            .toDto()

    @RequestMapping(value = ["/lifemanagement/api/logs"], method = [RequestMethod.POST], produces = ["application/json" ])
    override fun createLog(

        @Parameter(description = "The Data to create the log", required = true)
        @Valid @RequestBody
        log: CreateLogDto

    ): ReturnLogDto =
        log.toLoggingEvent()
            .let(logService::save)
            .toDto()
}
