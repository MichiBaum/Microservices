package com.michibaum.chess_service.app.event

import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.authentication_library.public_endpoints.PublicPattern
import com.michibaum.chess_service.app.game.GameConverter
import com.michibaum.chess_service.app.game.GameDto
import com.michibaum.chess_service.app.game.GameService
import com.michibaum.chess_service.app.person.PersonConverter
import com.michibaum.chess_service.app.person.PersonDto
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
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

    @PublicEndpoint
    @GetMapping("/api/events")
    fun getAllEvents(): List<EventDto> =
        eventService.findAllEagerCategories()
            .map { eventConverter.toDto(it) }

    @GetMapping("/api/events/search")
    fun searchEvents(@ModelAttribute param: SearchEventDto): List<EventDto> {
        return eventService.findAllBy(param.getSpecification(), param.getPageable()).content
            .map { eventConverter.toDto(it) }
    }

    @PublicEndpoint
    @GetMapping("/api/events/recent-upcoming")
    fun getAllRecentAndUpcomingEvents(): List<EventDto> {
        val recent = LocalDate.now().minusMonths(1)
        val upcoming = LocalDate.now().plusMonths(2)
        return eventService.findRecentAndUpcoming(recent, upcoming)
            .map { eventConverter.toDto(it) }
    }

    @PublicEndpoint(PublicPattern.UUID)
    @GetMapping("/api/events/{id}")
    fun getEvent(@PathVariable id: String): ResponseEntity<EventDto>{
        return try {
            val uuid = UUID.fromString(id)
            val event = eventService.findEagerCategories(uuid) ?:
                return ResponseEntity.notFound().build()
            val dto = eventConverter.toDto(event)
            return ResponseEntity.ok(dto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @PublicEndpoint(PublicPattern.UUID)
    @GetMapping("/api/events/{id}/participants")
    fun getEventParticipants(@PathVariable id: String): ResponseEntity<List<PersonDto>> {
        return try {
            val uuid = UUID.fromString(id)
            val event = eventService.findEagerParticipants(uuid) ?:
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

    @PublicEndpoint(PublicPattern.UUID)
    @GetMapping("/api/events/{id}/games")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.REPEATABLE_READ)
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

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    @PutMapping("/api/events") // TODO change to POST
    fun createEvent(@Valid @RequestBody eventDto: WriteEventDto): ResponseEntity<EventDto>{
        return try {
            val event = eventService.create(eventDto)
            val newEventDto = eventConverter.toDto(event)
            ResponseEntity(newEventDto, HttpStatus.CREATED)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    @PutMapping("/api/events/{id}")
    fun updateEvent(@PathVariable id: String, @Valid @RequestBody eventDto: WriteEventDto): ResponseEntity<EventDto> {
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