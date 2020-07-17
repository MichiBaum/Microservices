package com.michibaum.lifemanagementbackend.logs.repository

import com.michibaum.lifemanagementbackend.logs.domain.LoggingEvent
import com.michibaum.lifemanagementbackend.core.repository.CustomJpaRepository

interface LoggingEventRepository : CustomJpaRepository<LoggingEvent, Long> {
    fun findBySeenAndLevelStringIn(seen: Boolean, level: List<String>): List<LoggingEvent>
}

