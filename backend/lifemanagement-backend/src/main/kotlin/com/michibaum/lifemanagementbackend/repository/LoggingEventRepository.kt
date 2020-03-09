package com.michibaum.lifemanagementbackend.repository

import com.michibaum.lifemanagementbackend.domain.LoggingEvent

interface LoggingEventRepository : CustomJpaRepository<LoggingEvent, Long> {
    fun findBySeenAndLevelStringIn(seen: Boolean, level: List<String>): List<LoggingEvent>
}

