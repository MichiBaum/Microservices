package com.michibaum.lifemanagementbackend.service

import com.michibaum.lifemanagementbackend.domain.LoggingEvent
import com.michibaum.lifemanagementbackend.dtos.LogFilter
import com.michibaum.lifemanagementbackend.repository.LoggingEventRepository
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
