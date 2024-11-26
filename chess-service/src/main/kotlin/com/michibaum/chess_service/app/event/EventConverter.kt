package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.domain.Event
import com.michibaum.chess_service.domain.Person
import org.springframework.stereotype.Component

@Component
class EventConverter {

    fun toDto(event: Event): EventDto {
        return EventDto(
            id = event.id.toString(),
            title = event.title,
            url = event.url,
            embedUrl = event.embedUrl,
            dateFrom = event.dateFrom.toString(),
            dateTo = event.dateTo.toString()
        )
    }

    fun toDto(person: Person): ParticipantDto {
        return ParticipantDto(
            id = person.id.toString(),
            firstName = person.firstname,
            lastName = person.lastname,
            fideId = person.fideId
        )
    }

}