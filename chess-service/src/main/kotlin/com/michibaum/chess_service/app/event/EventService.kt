package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.domain.Event
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class EventService(
    private val eventRepository: EventRepository
) {

    fun findAll(): List<Event> =
        eventRepository.findAll()

    fun find(id: UUID): Event? =
        eventRepository.findById(id).getOrNull()

}