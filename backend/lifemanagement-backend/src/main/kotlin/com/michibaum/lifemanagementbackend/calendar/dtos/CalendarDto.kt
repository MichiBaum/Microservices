package com.michibaum.lifemanagementbackend.calendar.dtos

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Return Calendar DTO")
data class ReturnCalendarDto(

    @field:Schema(
        example = "5",
        required = true,
        description = "The id of the calendar"
    )
    val id: Long,

    @field:Schema(
        example = "SomeCalendarName",
        required = true,
        description = "The name of the calendar"
    )
    val name: String,

    @field:Schema(
        example = "This is a description of a calendar",
        required = true,
        description = "The description of the calendar"
    )
    val description: String

)
