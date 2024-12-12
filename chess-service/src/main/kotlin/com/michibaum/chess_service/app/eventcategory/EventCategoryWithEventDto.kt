package com.michibaum.chess_service.app.eventcategory

import com.michibaum.chess_service.app.event.EventDto

data class EventCategoryWithEventDto(
    val id: String,
    val title: String,
    val description: String,
    val events: List<EventDto>
)
