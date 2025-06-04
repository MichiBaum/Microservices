package com.michibaum.chess_service.app.event

import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.authentication_library.public_endpoints.PublicPattern
import com.michibaum.chess_service.app.eventcategory.EventCategoryConverter
import com.michibaum.chess_service.app.eventcategory.EventCategoryService
import com.michibaum.chess_service.app.game.GameConverter
import com.michibaum.chess_service.app.game.GameDto
import com.michibaum.chess_service.app.game.GameService
import com.michibaum.chess_service.app.person.PersonConverter
import com.michibaum.chess_service.app.person.PersonDto
import com.michibaum.chess_service.app.person.PersonService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*
import kotlin.collections.get

@RestController
class EventController(
    private val eventService: EventService,
    private val eventConverter: EventConverter,
    private val personConverter: PersonConverter,
    private val gameService: GameService,
    private val gameConverter: GameConverter,
    private val personService: PersonService,
    private val eventCategoryService: EventCategoryService,
    private val eventCategoryConverter: EventCategoryConverter
) {

    @PublicEndpoint
    @GetMapping("/api/events")
    fun getAllEvents(): List<EventDto> {
        val events = eventService.findAll()
        val eventCategoryMappings = eventCategoryService.findMappings(events)
        val eventCategories = eventCategoryService.findAllById(eventCategoryMappings.map { it.categoryId }.distinct())
            .map { eventCategoryConverter.toDto(it) }
        val eventCategoriesMap = eventCategoryMappings
            .groupBy({ it.eventId }, { category -> eventCategories.find { it.id == category.categoryId } })
            .mapValues { it.value.filterNotNull().toSet() }

        return events.map { event ->
            eventConverter.toDto(event, eventCategoriesMap[event.id] ?: emptySet())
        }
    }

    @GetMapping("/api/events/search")
    fun searchEvents(@ModelAttribute param: SearchEventDto): List<EventDto> {
        val events =  eventService.findAllBy(param.getSpecification(), param.getPageable()).content
        val eventCategoryMappings = eventCategoryService.findMappings(events)
        val eventCategories = eventCategoryService.findAllById(eventCategoryMappings.map { it.categoryId }.distinct())
            .map { eventCategoryConverter.toDto(it) }
        val eventCategoriesMap = eventCategoryMappings
            .groupBy({ it.eventId }, { category -> eventCategories.find { it.id == category.categoryId } })
            .mapValues { it.value.filterNotNull().toSet() }

        return events.map { event ->
            eventConverter.toDto(event, eventCategoriesMap[event.id] ?: emptySet())
        }
    }

    @PublicEndpoint
    @GetMapping("/api/events/recent-upcoming")
    fun getAllRecentAndUpcomingEvents(): List<EventDto> {
        val recent = LocalDate.now().minusMonths(1)
        val upcoming = LocalDate.now().plusMonths(2)
        val events =  eventService.findRecentAndUpcoming(recent, upcoming)
        val eventCategoryMappings = eventCategoryService.findMappings(events)
        val eventCategories = eventCategoryService.findAllById(eventCategoryMappings.map { it.categoryId }.distinct())
            .map { eventCategoryConverter.toDto(it) }
        val eventCategoriesMap = eventCategoryMappings
            .groupBy({ it.eventId }, { category -> eventCategories.find { it.id == category.categoryId } })
            .mapValues { it.value.filterNotNull().toSet() }

        return events.map { event ->
            eventConverter.toDto(event, eventCategoriesMap[event.id] ?: emptySet())
        }
    }

    @PublicEndpoint(PublicPattern.UUID)
    @GetMapping("/api/events/{id}")
    fun getEvent(@PathVariable id: String): ResponseEntity<EventDto>{
        return try {
            val uuid = UUID.fromString(id)
            val event = eventService.find(uuid) ?:
                return ResponseEntity.notFound().build()
            val eventCategoryMappings = eventCategoryService.findMappings(event)
            val eventCategories = eventCategoryService.findAllById(eventCategoryMappings.map { it.categoryId }.distinct())
                .map { eventCategoryConverter.toDto(it) }
                .toSet()
            val dto = eventConverter.toDto(event, eventCategories)
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
            val event = eventService.find(uuid) ?:
                return ResponseEntity.notFound().build()
            
            val participants = personService.findByEvent(event)
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
            val eventCategoryMappings = eventCategoryService.findMappings(event)
            val eventCategories = eventCategoryService.findAllById(eventCategoryMappings.map { it.categoryId }.distinct())
                .map { eventCategoryConverter.toDto(it) }
                .toSet()
            val newEventDto = eventConverter.toDto(event, eventCategories)
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
            val eventCategoryMappings = eventCategoryService.findMappings(newEvent)
            val eventCategories = eventCategoryService.findAllById(eventCategoryMappings.map { it.categoryId }.distinct())
                .map { eventCategoryConverter.toDto(it) }
                .toSet()
            val newEventDto = eventConverter.toDto(event, eventCategories)
            ResponseEntity.ok(newEventDto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

}