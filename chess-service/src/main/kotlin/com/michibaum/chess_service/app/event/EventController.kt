package com.michibaum.chess_service.app.event

import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@RestController
class EventController(
    private val eventService: EventService,
    private val eventConverter: EventConverter
) {

    @GetMapping("/api/events")
    @Transactional
    fun getAllEvents(): List<EventDto> =
        eventService.findAll()
            .map { eventConverter.toDto(it) }

    @GetMapping("/api/events/recent-upcoming")
    @Transactional
    fun getAllRecentAndUpcomingEvents(): List<EventDto> {
        val recent = LocalDate.now().minusMonths(2)
        val upcoming = LocalDate.now().plusMonths(2)
        return eventService.findRecentAndUpcoming(recent, upcoming)
            .map { eventConverter.toDto(it) }
    }

    @GetMapping("/api/events/{id}")
    @Transactional
    fun getEvent(@PathVariable id: String): ResponseEntity<EventDto>{
        return try {
            val uuid = UUID.fromString(id)
            val event = eventService.find(uuid) ?:
                return ResponseEntity.notFound().build()
            val dto = eventConverter.toDto(event)
            return ResponseEntity.ok(dto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/api/events/{id}/participants")
    @Transactional
    fun getEventParticipants(@PathVariable id: String): ResponseEntity<List<ParticipantDto>> {
        return try {
            val uuid = UUID.fromString(id)
            val event = eventService.find(uuid) ?:
            return ResponseEntity.notFound().build()
            val participants = event.participants
                .map { participantDto -> eventConverter.toDto(participantDto) }
            return ResponseEntity.ok().body(participants)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/api/events/categories")
    fun getEventCategories(): ResponseEntity<List<EventCategoryDto>> {
        val categoryDtos = eventService.allCategories()
            .map(eventConverter::toDto)
        return ResponseEntity.ok().body(categoryDtos)
    }

}