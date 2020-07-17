package com.michibaum.lifemanagementbackend.calendar.service

import com.michibaum.lifemanagementbackend.calendar.domain.Calendar
import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.calendar.repository.CalendarRepository
import org.springframework.stereotype.Service

@Service
class CalendarService(
    private val calendarRepository: CalendarRepository
) {

    fun findByUser(user: User): List<Calendar> =
        calendarRepository.findByUsers(user)

    fun findAll(): List<Calendar> =
        calendarRepository.findAll()

}
