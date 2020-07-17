package com.michibaum.lifemanagementbackend.repository

import com.michibaum.lifemanagementbackend.domain.Calendar
import com.michibaum.lifemanagementbackend.domain.User

interface CalendarRepository : CustomJpaRepository<Calendar, Long> {
    fun findByUsers(user: User): List<Calendar>
}
