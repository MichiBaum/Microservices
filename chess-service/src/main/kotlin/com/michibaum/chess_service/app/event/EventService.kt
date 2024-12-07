package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.app.person.PersonRepository
import com.michibaum.chess_service.domain.Event
import com.michibaum.chess_service.domain.EventCategory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val eventCategoryRepository: EventCategoryRepository,
    private val personRepository: PersonRepository
) {

    fun findAll(): List<Event> =
        eventRepository.findAll()

    fun find(id: UUID): Event? =
        eventRepository.findById(id).getOrNull()

    fun findRecentAndUpcoming(recent: LocalDate, upcoming: LocalDate): List<Event> =
        eventRepository.findByDateFromGreaterThanAndDateToLessThan(recent, upcoming)

    fun allCategories(): List<EventCategory> {
        return eventCategoryRepository.findAll()
    }

    fun create(dto: WriteEventDto): Event {
        val categoryIds = dto.categoryIds.map { UUID.fromString(it) }
        val newCategories = eventCategoryRepository.findAllById(categoryIds).toMutableSet()

        val participantsIds = dto.participantsIds.map { UUID.fromString(it) }
        val newParticipants = personRepository.findAllById(participantsIds).toMutableSet()

        val event = Event(
            title = dto.title,
            location = dto.location,
            url = dto.url,
            embedUrl = dto.embedUrl,
            dateFrom = LocalDate.parse(dto.dateFrom),
            dateTo = LocalDate.parse(dto.dateTo),
            categories = newCategories,
            participants = newParticipants
        )
        return eventRepository.save(event)
    }

    fun update(event: Event, dto: WriteEventDto): Event {
        val categoryIds = dto.categoryIds.map { UUID.fromString(it) }
        val newCategories = eventCategoryRepository.findAllById(categoryIds).toMutableSet()

        val participantsIds = dto.participantsIds.map { UUID.fromString(it) }
        val newParticipants = personRepository.findAllById(participantsIds).toMutableSet()

        val newEvent = Event(
            title = dto.title,
            location = dto.location,
            url = dto.url,
            embedUrl = dto.embedUrl,
            dateFrom = LocalDate.parse(dto.dateFrom),
            dateTo = LocalDate.parse(dto.dateTo),
            categories = newCategories,
            participants = newParticipants,
            id = event.id
        )

        return eventRepository.save(newEvent)
    }
}