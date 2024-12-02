package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.app.person.PersonConverter
import com.michibaum.chess_service.domain.Event
import com.michibaum.chess_service.domain.EventCategory
import com.michibaum.chess_service.domain.Person
import org.springframework.stereotype.Component

@Component
class EventConverter(
    private val personConverter: PersonConverter
) {

    fun toDto(event: Event): EventDto {
        return EventDto(
            id = event.id.toString(),
            title = event.title,
            url = event.url,
            embedUrl = event.embedUrl,
            dateFrom = event.dateFrom.toString(),
            dateTo = event.dateTo.toString(),
            categories = event.categories.map { toDto(it) },
            participants = event.participants.map { personConverter.convert(it) }
        )
    }

    fun toDto(category: EventCategory): EventCategoryDto {
        return EventCategoryDto(
            id = category.id.toString(),
            title = category.name,
            description = category.description,
        )
    }

}