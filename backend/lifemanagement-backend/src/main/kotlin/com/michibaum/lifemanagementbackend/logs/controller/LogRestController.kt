package com.michibaum.lifemanagementbackend.logs.controller

import com.michibaum.lifemanagementbackend.logs.converter.toDto
import com.michibaum.lifemanagementbackend.logs.converter.toLoggingEvent
import com.michibaum.lifemanagementbackend.logs.domain.LoggingEvent
import com.michibaum.lifemanagementbackend.logs.dtos.LogFilter
import com.michibaum.lifemanagementbackend.logs.dtos.ReturnLogDto
import com.michibaum.lifemanagementbackend.logs.dtos.UpdateLogDto
import com.michibaum.lifemanagementbackend.logs.service.LogService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class LogRestController(
    logService: LogService
) {

    private val findAllLogs: () -> List<LoggingEvent> = logService.findAll
    private val findLogsByFilter: (LogFilter) -> List<LoggingEvent> = logService.findByFilter
    private val saveLog: (LoggingEvent) -> LoggingEvent = logService.save
    private val changeLoggingEventSeen: (LoggingEvent, Boolean) -> LoggingEvent = logService.changeSeen

    @PreAuthorize("hasAuthority('SEE_LOGS')")
    @RequestMapping(value = ["/lifemanagement/api/logs"], method = [RequestMethod.GET])
    fun getLogs(logFilter: LogFilter): List<ReturnLogDto> =
        findLogsByFilter(logFilter).map(LoggingEvent::toDto)

    @PreAuthorize("hasAuthority('SEE_LOGS')")
    @RequestMapping(value = ["/lifemanagement/api/logs/levels"], method = [RequestMethod.GET])
    fun getLogLevels(): List<String> =
        findAllLogs().map(LoggingEvent::levelString).distinct()

    @PreAuthorize("hasAuthority('SEE_LOGS')")
    @RequestMapping(value = ["/lifemanagement/api/logs/{loggingEvent}/seen"], method = [RequestMethod.POST])
    fun setLogSeen(@PathVariable loggingEvent: LoggingEvent, @RequestBody seen: Boolean) =
        changeLoggingEventSeen.invoke(loggingEvent, seen).let(saveLog).toDto()

    @RequestMapping(value = ["/lifemanagement/api/logs"], method = [RequestMethod.POST])
    fun addLog(@RequestBody log: UpdateLogDto) =
        log.toLoggingEvent().let(saveLog).toDto()
}
