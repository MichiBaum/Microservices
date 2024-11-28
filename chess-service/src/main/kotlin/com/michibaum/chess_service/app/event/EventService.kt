package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.domain.Event
import com.michibaum.chess_service.domain.EventCategory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val eventCategoryRepository: EventCategoryRepository
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

}