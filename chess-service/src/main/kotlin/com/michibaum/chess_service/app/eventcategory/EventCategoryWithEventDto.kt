package com.michibaum.chess_service.app.eventcategory

import com.michibaum.chess_service.app.event.EventDto
import java.util.UUID

data class EventCategoryWithEventDto(
    val id: UUID,
    val title: String,
    val description: String,
    val events: List<EventDto>
)
