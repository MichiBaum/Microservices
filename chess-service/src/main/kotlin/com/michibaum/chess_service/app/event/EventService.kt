package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.app.person.PersonRepository
import com.michibaum.chess_service.domain.Event
import com.michibaum.chess_service.domain.EventCategory
import com.michibaum.chess_service.domain.Person
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
        val categories = getCategories(dto.categoryIds)
        val participants = getPersons(dto.participantsIds)

        val event = Event(
            title = dto.title,
            location = dto.location,
            url = dto.url,
            embedUrl = dto.embedUrl,
            dateFrom = LocalDate.parse(dto.dateFrom),
            dateTo = LocalDate.parse(dto.dateTo),
            categories = categories,
            participants = participants
        )
        return eventRepository.save(event)
    }

    fun update(event: Event, dto: WriteEventDto): Event {
        val categories = getCategories(dto.categoryIds)
        val participants = getPersons(dto.participantsIds)

        val newEvent = Event(
            title = dto.title,
            location = dto.location,
            url = dto.url,
            embedUrl = dto.embedUrl,
            dateFrom = LocalDate.parse(dto.dateFrom),
            dateTo = LocalDate.parse(dto.dateTo),
            categories = categories,
            participants = participants,
            id = event.id
        )

        return eventRepository.save(newEvent)
    }

    private fun getPersons(ids: List<String>): MutableSet<Person> {
        val participantsIds = ids.map { UUID.fromString(it) }
        return personRepository.findAllById(participantsIds).toMutableSet()
    }

    private fun getCategories(ids: List<String>): Set<EventCategory> {
        val categoryIds = ids.map { UUID.fromString(it) }
        return eventCategoryRepository.findAllById(categoryIds).toSet()
    }
}