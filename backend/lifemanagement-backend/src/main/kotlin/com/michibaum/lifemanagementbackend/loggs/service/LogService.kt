package com.michibaum.lifemanagementbackend.loggs.service

import com.michibaum.lifemanagementbackend.loggs.domain.LoggingEvent
import com.michibaum.lifemanagementbackend.loggs.dtos.LogFilter
import com.michibaum.lifemanagementbackend.loggs.repository.LoggingEventRepository
import org.springframework.stereotype.Service

@Service
class LogService(
    loggingEventRepository: LoggingEventRepository
) {

    val findByFilter = fun(logFilter: LogFilter): List<LoggingEvent> =
        loggingEventRepository.findBySeenAndLevelStringIn(logFilter.seen, logFilter.level)

    val save: (LoggingEvent) -> LoggingEvent =
        loggingEventRepository::saveAndFlush

    val findAll: () -> List<LoggingEvent> =
        loggingEventRepository::findAll

    val changeSeen = fun(logginEvent: LoggingEvent, seen: Boolean): LoggingEvent =
        logginEvent.apply { this.seen = seen }
}
