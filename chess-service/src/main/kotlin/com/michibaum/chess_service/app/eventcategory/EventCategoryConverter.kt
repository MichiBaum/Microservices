package com.michibaum.chess_service.app.eventcategory

import com.michibaum.chess_service.app.event.EventDto
import com.michibaum.chess_service.database.EventCategory
import org.springframework.stereotype.Component

@Component
class EventCategoryConverter(
) {

    fun toDto(category: EventCategory): EventCategoryDto {
        return EventCategoryDto(
            id = category.idOrThrow(),
            title = category.name,
            description = category.description,
        )
    }

    fun toDto(category: EventCategory, events: List<EventDto>): EventCategoryWithEventDto{
        return EventCategoryWithEventDto(
            id = category.idOrThrow(),
            title = category.name,
            description = category.description,
            events = events
        )
    }

}