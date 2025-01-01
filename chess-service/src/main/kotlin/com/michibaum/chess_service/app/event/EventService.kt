package com.michibaum.chess_service.app.event

import com.michibaum.authentication_library.anyOf
import com.michibaum.chess_service.app.eventcategory.EventCategoryService
import com.michibaum.chess_service.app.person.PersonRepository
import com.michibaum.chess_service.domain.Event
import com.michibaum.chess_service.domain.EventCategory
import com.michibaum.chess_service.domain.Person
import com.michibaum.permission_library.Permissions
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val eventCategoryService: EventCategoryService,
    private val personRepository: PersonRepository
) {

    fun findAll(): List<Event> =
        eventRepository.findAll()

    fun find(id: UUID): Event? =
        eventRepository.findById(id).getOrNull()

    fun findRecentAndUpcoming(recent: LocalDate, upcoming: LocalDate): List<Event> =
        eventRepository.findByDateFromGreaterThanAndDateToLessThan(recent, upcoming)

    fun create(dto: WriteEventDto): Event {
        val categories = getCategories(dto.categoryIds)
        val participants = getPersons(dto.participantsIds)

        val authentication = SecurityContextHolder.getContext().authentication
        val internalComment = if (authentication != null && authentication.anyOf(Permissions.CHESS_SERVICE_ADMIN))
            dto.internalComment
        else
            ""

        val event = Event(
            title = dto.title,
            location = dto.location,
            url = dto.url,
            embedUrl = dto.embedUrl,
            dateFrom = LocalDate.parse(dto.dateFrom),
            dateTo = LocalDate.parse(dto.dateTo),
            internalComment = internalComment,
            categories = categories,
            participants = participants
        )
        return eventRepository.save(event)
    }

    fun update(event: Event, dto: WriteEventDto): Event {
        val categories = getCategories(dto.categoryIds)
        val participants = getPersons(dto.participantsIds)

        val authentication = SecurityContextHolder.getContext().authentication
        val internalComment = if (authentication != null && authentication.anyOf(Permissions.CHESS_SERVICE_ADMIN))
            dto.internalComment
        else
            event.internalComment

        val newEvent = Event(
            title = dto.title,
            location = dto.location,
            url = dto.url,
            embedUrl = dto.embedUrl,
            dateFrom = LocalDate.parse(dto.dateFrom),
            dateTo = LocalDate.parse(dto.dateTo),
            internalComment = internalComment,
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
        return eventCategoryService.findAllById(categoryIds).toSet()
    }

    fun findByCategoryId(id: UUID): Set<Event> =
        eventRepository.findByCategoriesId(id)

    fun findAllBy(specification: Specification<Event>, pageable: PageRequest): Page<Event> {
        return eventRepository.findAll(specification, pageable)
    }
}