package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.app.game.GameConverter
import com.michibaum.chess_service.app.game.GameDto
import com.michibaum.chess_service.app.game.GameService
import com.michibaum.chess_service.app.person.PersonConverter
import com.michibaum.chess_service.app.person.PersonDto
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@RestController
class EventController(
    private val eventService: EventService,
    private val eventConverter: EventConverter,
    private val personConverter: PersonConverter,
    private val gameService: GameService,
    private val gameConverter: GameConverter
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
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/api/events/{id}/participants")
    @Transactional
    fun getEventParticipants(@PathVariable id: String): ResponseEntity<List<PersonDto>> {
        return try {
            val uuid = UUID.fromString(id)
            val event = eventService.find(uuid) ?:
                return ResponseEntity.notFound().build()
            val participants = event.participants
                .map { participant -> personConverter.convert(participant) }
            return ResponseEntity.ok().body(participants)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/api/events/{id}/games")
    fun getEventGames(@PathVariable id: String): ResponseEntity<List<GameDto>> {
        return try {
            val uuid = UUID.fromString(id)
            val event = eventService.find(uuid) ?:
                return ResponseEntity.notFound().build()
            val games = gameService.getByEvent(event)
            val dtos = games.map { gameConverter.convert(it) }
            return ResponseEntity.ok().body(dtos)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/api/events/categories")
    fun getEventCategories(): ResponseEntity<List<EventCategoryDto>> {
        val categoryDtos = eventService.allCategories()
            .map(eventConverter::toDto)
        return ResponseEntity.ok().body(categoryDtos)
    }

    @Transactional
    @PutMapping("/api/events")
    fun createEvent(@RequestBody eventDto: WriteEventDto): ResponseEntity<EventDto>{
        return try {
            val event = eventService.create(eventDto)
            val newEventDto = eventConverter.toDto(event)
            ResponseEntity.ok(newEventDto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @Transactional
    @PutMapping("/api/events/{id}")
    fun updateEvent(@PathVariable id: String, @RequestBody eventDto: WriteEventDto): ResponseEntity<EventDto> {
        return try {
            val uuid = UUID.fromString(id)
            val event = eventService.find(uuid) ?:
                return ResponseEntity.notFound().build()
            val newEvent = eventService.update(event, eventDto)
            val newEventDto = eventConverter.toDto(newEvent)
            ResponseEntity.ok(newEventDto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

}