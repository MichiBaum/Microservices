package com.michibaum.lifemanagementbackend.logs.service

import com.michibaum.lifemanagementbackend.logs.domain.LoggingEvent
import com.michibaum.lifemanagementbackend.logs.dtos.LogFilter
import com.michibaum.lifemanagementbackend.logs.repository.LoggingEventRepository
import org.springframework.stereotype.Service

@Service
class LogService(
    private val loggingEventRepository: LoggingEventRepository
) {

    fun findByFilter(logFilter: LogFilter): List<LoggingEvent> =
        loggingEventRepository.findBySeenAndLevelStringIn(logFilter.seen, logFilter.level)

    fun save(logginEvent: LoggingEvent): LoggingEvent =
        loggingEventRepository.saveAndFlush(logginEvent)

    fun findAll(): List<LoggingEvent> =
        loggingEventRepository.findAll()

    fun changeSeen(logginEvent: LoggingEvent, seen: Boolean): LoggingEvent =
        logginEvent.apply { this.seen = seen }
}
