package com.michibaum.lifemanagementbackend.converter

import com.michibaum.lifemanagementbackend.domain.LoggingEvent
import com.michibaum.lifemanagementbackend.dtos.ReturnLogDto
import com.michibaum.lifemanagementbackend.dtos.UpdateLogDto

fun LoggingEvent.toDto() = ReturnLogDto(
    id = eventId,
    date = timestmp,
    formattedMessage = formattedMessage,
    loggerName = loggerName,
    level = levelString,
    threadName = threadName ?: "",
    arg0 = arg0 ?: "",
    arg1 = arg1 ?: "",
    arg2 = arg2 ?: "",
    arg3 = arg3 ?: "",
    callerFilename = callerFilename,
    callerClass = callerClass,
    callerMethod = callerMethod,
    callerLine = callerLine,
    seen = seen
)

fun UpdateLogDto.toLoggingEvent() = LoggingEvent(
    timestmp = date,
    formattedMessage = formattedMessage,
    loggerName = loggerName,
    levelString = level,
    threadName = threadName,
    arg0 = arg0,
    arg1 = arg1,
    arg2 = arg2,
    arg3 = arg3,
    callerFilename = callerFilename,
    callerClass = callerClass,
    callerMethod = callerMethod,
    callerLine = callerLine,
    seen = seen
)
