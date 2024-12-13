package com.michibaum.chess_service.app.eventcategory

import com.michibaum.chess_service.app.event.EventConverter
import com.michibaum.chess_service.app.event.EventService
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

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
    fun getEventCategoriesWithEvents(): ResponseEntity<List<EventCategoryWithEventDto>> {
        val dtos = service.getAll().map { category ->
            val eventDtos = eventService.findByCategoryId(category.idOrThrow()).map { event -> eventConverter.toDto(event) }
            converter.toDto(category, eventDtos)
        }.toList()
        return ResponseEntity.ok().body(dtos)
    }

}