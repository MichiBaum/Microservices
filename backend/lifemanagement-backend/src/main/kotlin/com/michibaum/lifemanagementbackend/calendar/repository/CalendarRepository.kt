package com.michibaum.lifemanagementbackend.calendar.repository

import com.michibaum.lifemanagementbackend.calendar.domain.Calendar
import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.core.repository.CustomJpaRepository

interface CalendarRepository : CustomJpaRepository<Calendar, Long> {
    fun findByUsers(user: User): List<Calendar>
}
