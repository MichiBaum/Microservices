package com.michibaum.chess_service.app.event

import com.michibaum.authentication_library.Security
import com.michibaum.authentication_library.anyOf
import com.michibaum.chess_service.app.eventcategory.EventCategoryService
import com.michibaum.chess_service.database.PersonRepository
import com.michibaum.chess_service.database.Event
import com.michibaum.chess_service.database.EventCategory
import com.michibaum.chess_service.database.EventCategoryMapping
import com.michibaum.chess_service.database.EventCategoryMappingProjection
import com.michibaum.chess_service.database.EventCategoryMappingRepository
import com.michibaum.chess_service.database.EventParticipantMappingRepository
import com.michibaum.chess_service.database.EventParticipantsMapping
import com.michibaum.chess_service.database.EventRepository
import com.michibaum.chess_service.database.Person
import com.michibaum.permission_library.Permissions
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val eventCategoryService: EventCategoryService,
    private val eventCategoryMappingRepository: EventCategoryMappingRepository,
    private val personRepository: PersonRepository,
    private val eventParticipantMappingRepository: EventParticipantMappingRepository
) {

    fun findAll(): List<Event> =
        eventRepository.findAll()

    fun find(id: UUID): Event? =
        eventRepository.findByEventId(id)

    fun findRecentAndUpcoming(recent: LocalDate, upcoming: LocalDate): List<Event> =
        eventRepository.findByDateFromGreaterThanAndDateToLessThan(recent, upcoming)

    @Transactional
    fun create(dto: WriteEventDto): Event {
        val categories = getCategories(dto.categoryIds)
        val participants = getPersons(dto.participantsIds)

        val authentication = Security.authentication
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
            platform = dto.platform,
        )

        val savedNewEvent = eventRepository.save(event)
        updateEventCategoryMapping(savedNewEvent, categories)
        updateEventparticipantsMapping(savedNewEvent, participants)

        return savedNewEvent
    }
    

    @Transactional
    fun update(event: Event, dto: WriteEventDto): Event {
        val categories = getCategories(dto.categoryIds)
        val participants = getPersons(dto.participantsIds)

        val authentication = Security.authentication
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
            platform = dto.platform,
            id = event.id
        )

        val savedNewEvent = eventRepository.save(newEvent)
        updateEventCategoryMapping(savedNewEvent, categories)
        updateEventparticipantsMapping(savedNewEvent, participants)
        
        return savedNewEvent
    }
    
    @Transactional
    fun updateEventCategoryMapping(event: Event, categories: Set<EventCategory>): List<EventCategoryMapping?> {
        eventCategoryMappingRepository.deleteByEvent(event)
        val newMappings = categories.map { EventCategoryMapping(event, it) }
        if(newMappings.isEmpty())
            return emptyList()
        return eventCategoryMappingRepository.saveAll(newMappings)
    }
    
    @Transactional
    fun updateEventparticipantsMapping(event: Event, participants: Set<Person>): List<EventParticipantsMapping> {
        eventParticipantMappingRepository.deleteByEvent(event)
        val newMappings = participants.map { EventParticipantsMapping(event, it) }
        if(newMappings.isEmpty()){
            return emptyList()
        }
        return eventParticipantMappingRepository.saveAll(newMappings)
        
    }

    private fun getPersons(ids: List<String>): Set<Person> {
        val participantsIds = ids.map { UUID.fromString(it) }
        return personRepository.findAllById(participantsIds).toSet()
    }

    private fun getCategories(ids: List<String>): Set<EventCategory> {
        val categoryIds = ids.map { UUID.fromString(it) }
        return eventCategoryService.findAllById(categoryIds).toSet()
    }

    fun findAllBy(specification: Specification<Event>, pageable: PageRequest): Page<Event> {
        return eventRepository.findAll(specification, pageable)
    }

}