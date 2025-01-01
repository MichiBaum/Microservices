package com.michibaum.chess_service.app.event

import com.michibaum.authentication_library.anyOf
import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.chess_service.app.eventcategory.EventCategoryConverter
import com.michibaum.chess_service.app.person.PersonConverter
import com.michibaum.chess_service.domain.Event
import com.michibaum.permission_library.Permissions
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class EventConverter(
    private val personConverter: PersonConverter,
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
            categories = event.categories.map { eventCategoryConverter.toDto(it) },
            participants = event.participants.map { personConverter.convert(it) }
        )
    }

    private fun getInternalComment(comment: String): String {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication != null && authentication.anyOf(Permissions.CHESS_SERVICE_ADMIN)) comment else ""
    }

}