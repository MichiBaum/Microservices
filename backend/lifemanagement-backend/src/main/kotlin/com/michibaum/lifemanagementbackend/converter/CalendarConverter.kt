package com.michibaum.lifemanagementbackend.converter

import com.michibaum.lifemanagementbackend.domain.Calendar
import com.michibaum.lifemanagementbackend.dtos.ReturnCalendarDto

fun Calendar.toDto(): ReturnCalendarDto =
    ReturnCalendarDto(
        id = id,
        name = name,
        description = description
    )
