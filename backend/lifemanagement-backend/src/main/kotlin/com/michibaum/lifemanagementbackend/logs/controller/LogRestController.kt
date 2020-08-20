package com.michibaum.lifemanagementbackend.logs.controller

import com.michibaum.lifemanagementbackend.logs.converter.toDto
import com.michibaum.lifemanagementbackend.logs.converter.toLoggingEvent
import com.michibaum.lifemanagementbackend.logs.domain.LoggingEvent
import com.michibaum.lifemanagementbackend.logs.dtos.LogFilter
import com.michibaum.lifemanagementbackend.logs.dtos.ReturnLogDto
import com.michibaum.lifemanagementbackend.logs.dtos.CreateLogDto
import com.michibaum.lifemanagementbackend.logs.service.LogService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class LogRestController(
    private val logService: LogService
): LogRestControllerDocs {

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasAuthority('SEE_LOGS')")
    @RequestMapping(value = ["/lifemanagement/api/logs"], method = [RequestMethod.GET], produces = ["application/json" ])
    override fun getLogs(logFilter: LogFilter): List<ReturnLogDto> =
        logService.findByFilter(logFilter)
            .map(LoggingEvent::toDto)

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasAuthority('SEE_LOGS')")
    @RequestMapping(value = ["/lifemanagement/api/logs/levels"], method = [RequestMethod.GET], produces = ["application/json" ])
    override fun getLogLevels(): List<String> =
        logService.findAll()
            .map(LoggingEvent::levelString)
            .distinct()

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasAuthority('SEE_LOGS')")
    @RequestMapping(value = ["/lifemanagement/api/logs/{loggingEvent}/seen"], method = [RequestMethod.POST], produces = ["application/json" ])
    override fun setLogSeen(@PathVariable loggingEvent: LoggingEvent, @RequestBody seen: Boolean): ReturnLogDto =
        logService.changeSeen(loggingEvent, seen)
            .let(logService::save)
            .toDto()

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    @RequestMapping(value = ["/lifemanagement/api/logs"], method = [RequestMethod.POST], produces = ["application/json" ])
    override fun createLog(@Valid @RequestBody log: CreateLogDto): ReturnLogDto =
        log.toLoggingEvent()
            .let(logService::save)
            .toDto()
}
