package com.michibaum.lifemanagementbackend.loggs.repository

import com.michibaum.lifemanagementbackend.loggs.domain.LoggingEvent
import com.michibaum.lifemanagementbackend.core.repository.CustomJpaRepository

interface LoggingEventRepository :
    CustomJpaRepository<LoggingEvent, Long> {
    fun findBySeenAndLevelStringIn(seen: Boolean, level: List<String>): List<LoggingEvent>
}

