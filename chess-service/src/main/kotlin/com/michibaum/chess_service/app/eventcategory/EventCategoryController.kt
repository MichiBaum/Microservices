package com.michibaum.chess_service.app.eventcategory

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
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

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.REPEATABLE_READ)
    @GetMapping("/api/event-categories")
    fun getEventCategories(): ResponseEntity<List<EventCategoryDto>> {
        val categoryDtos = service.getAll()
            .map(converter::toDto)
        return ResponseEntity.ok().body(categoryDtos)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.REPEATABLE_READ)
    @GetMapping("/api/event-categories/with-events")
    fun getEventCategoriesWithEvents(principal: JwtAuthentication): ResponseEntity<List<EventCategoryWithEventDto>> {
        val dtos = service.getAll().map { category ->
            val eventDtos = eventService.findByCategoryId(category.idOrThrow()).map { event -> eventConverter.toDto(event) }
            converter.toDto(category, eventDtos)
        }.toList()
        return ResponseEntity.ok().body(dtos)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    @PutMapping("/api/event-categories")
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