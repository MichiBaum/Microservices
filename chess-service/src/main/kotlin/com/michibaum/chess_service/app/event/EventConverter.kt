package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.app.eventcategory.EventCategoryConverter
import com.michibaum.chess_service.app.person.PersonConverter
import com.michibaum.chess_service.domain.Event
import com.michibaum.chess_service.domain.EventCategory
import org.springframework.stereotype.Component

@Component
class EventConverter(
    private val personConverter: PersonConverter,
    private val eventCategoryConverter: EventCategoryConverter
) {

    fun toDto(event: Event): EventDto {
        return EventDto(
            id = event.id.toString(),
            title = event.title,
            location = event.location,
            url = event.url,
            embedUrl = event.embedUrl,
            dateFrom = event.dateFrom.toString(),
            dateTo = event.dateTo.toString(),
            categories = event.categories.map { eventCategoryConverter.toDto(it) },
            participants = event.participants.map { personConverter.convert(it) }
        )
    }

}