package com.michibaum.lifemanagementbackend.calendar.converter

import com.michibaum.lifemanagementbackend.calendar.domain.Calendar
import com.michibaum.lifemanagementbackend.calendar.dtos.ReturnCalendarDto

fun Calendar.toDto(): ReturnCalendarDto =
    ReturnCalendarDto(
        id = id,
        name = name,
        description = description
    )
