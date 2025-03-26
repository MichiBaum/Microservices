package com.michibaum.chess_service.app.eventcategory

import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.chess_service.app.event.EventConverter
import com.michibaum.chess_service.app.event.EventService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class EventCategoryController(
    private val service: EventCategoryService,
    private val converter: EventCategoryConverter,
    private val eventService: EventService,
    private val eventConverter: EventConverter
) {

    @PublicEndpoint
    @GetMapping("/api/event-categories")
    fun getEventCategories(): ResponseEntity<List<EventCategoryDto>> {
        val categoryDtos = service.getAll()
            .map(converter::toDto)
        return ResponseEntity.ok().body(categoryDtos)
    }

    @PublicEndpoint
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.REPEATABLE_READ)
    @GetMapping("/api/event-categories/with-events")
    fun getEventCategoriesWithEvents(): ResponseEntity<List<EventCategoryWithEventDto>> { // TODO custom sql query to fetch all in one
        val events = eventService.findAllEagerCategories()
        val result = eventConverter.toDto(events)
        return ResponseEntity.ok().body(result)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    @PutMapping("/api/event-categories") // TODO change to post
    fun createCategory(@Valid @RequestBody categoryDto: WriteEventCategoryDto): ResponseEntity<EventCategoryDto> {
        return try {
            val category = service.create(categoryDto)
            val dto = converter.toDto(category)
            ResponseEntity(dto, HttpStatus.CREATED)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    @PutMapping("/api/event-categories/{id}")
    fun updateCategory(@PathVariable id: String, @Valid @RequestBody categoryDto: WriteEventCategoryDto): ResponseEntity<EventCategoryDto> {
        return try {
            val uuid = UUID.fromString(id)
            val category = service.find(uuid) ?:
                return ResponseEntity.notFound().build()
            val newCategory = service.update(category, categoryDto)
            val dto = converter.toDto(newCategory)
            ResponseEntity.ok(dto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

}