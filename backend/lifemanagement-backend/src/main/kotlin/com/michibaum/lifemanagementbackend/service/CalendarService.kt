package com.michibaum.lifemanagementbackend.service

import com.michibaum.lifemanagementbackend.domain.Calendar
import com.michibaum.lifemanagementbackend.domain.User
import com.michibaum.lifemanagementbackend.repository.CalendarRepository
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
