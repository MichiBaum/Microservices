package com.michibaum.chess_service.app.event

import com.michibaum.authentication_library.Security
import com.michibaum.authentication_library.anyOf
import com.michibaum.chess_service.app.eventcategory.EventCategoryConverter
import com.michibaum.chess_service.app.eventcategory.EventCategoryWithEventDto
import com.michibaum.chess_service.database.Event
import com.michibaum.permission_library.Permissions
import org.springframework.stereotype.Component

@Component
class EventConverter(
    private val eventCategoryConverter: EventCategoryConverter
) {

    fun toDto(event: Event): EventDto {
        return EventDto(
            id = event.idOrThrow(),
            title = event.title,
            location = event.location,
            url = event.url,
            embedUrl = event.embedUrl,
            dateFrom = event.dateFrom.toString(),
            dateTo = event.dateTo.toString(),
            internalComment = getInternalComment(event.internalComment),
            platform = event.platform,
            categories = event.categories.map { eventCategoryConverter.toDto(it) },
        )
    }

    private fun getInternalComment(comment: String): String {
        val authentication = Security.authentication
        return if (authentication != null && authentication.anyOf(Permissions.CHESS_SERVICE_ADMIN)) comment else ""
    }

    fun toDto(events: List<Event>): List<EventCategoryWithEventDto> {
        val categories = events.flatMap { x -> x.categories }
            .distinct()
            .toList()

        val dtos = ArrayList<EventCategoryWithEventDto>()
        for(category in categories){
            val eventsForCategory = events.filter { event -> event.categories.contains(category) }
                .toList()
            val eventDtos = eventsForCategory.map { event -> toDto(event) }
            eventCategoryConverter.toDto(category, eventDtos).let {
                dtos.add(it)
            }
        }

        return dtos
    }

}