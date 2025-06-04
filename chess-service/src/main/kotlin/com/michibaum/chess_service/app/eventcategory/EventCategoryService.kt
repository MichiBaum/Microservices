package com.michibaum.chess_service.app.eventcategory

import com.michibaum.chess_service.database.Event
import com.michibaum.chess_service.database.EventCategory
import com.michibaum.chess_service.database.EventCategoryMappingProjection
import com.michibaum.chess_service.database.EventCategoryMappingRepository
import com.michibaum.chess_service.database.EventCategoryRepository
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class EventCategoryService(
    private val eventCategoryRepository: EventCategoryRepository,
    private val eventCategoryMappingRepository: EventCategoryMappingRepository
) {

    fun getAll(): List<EventCategory> =
        eventCategoryRepository.findAll()

    fun findAllById(categoryIds: List<UUID>): List<EventCategory> =
        eventCategoryRepository.findAllById(categoryIds)

    fun find(id: UUID): EventCategory? =
        eventCategoryRepository.findById(id).getOrNull()

    fun create(categoryDto: WriteEventCategoryDto): EventCategory {
        val category = EventCategory(
            name = categoryDto.title,
            description = categoryDto.description,
        )
        return eventCategoryRepository.save(category)
    }

    fun update(category: EventCategory, categoryDto: WriteEventCategoryDto): EventCategory {
        val newCategory = EventCategory(
            name = categoryDto.title,
            description = categoryDto.description,
            id = category.id
        )
        return eventCategoryRepository.save(newCategory)
    }

    fun findMappings(events: List<Event>): Set<EventCategoryMappingProjection> {
        return eventCategoryMappingRepository.findByEvents(events)
    }
    
    fun findMappings(event: Event): Set<EventCategoryMappingProjection> {
        return eventCategoryMappingRepository.findByEvents(event)
    }

}