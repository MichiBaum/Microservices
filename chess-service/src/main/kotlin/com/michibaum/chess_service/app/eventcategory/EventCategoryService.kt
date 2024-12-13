package com.michibaum.chess_service.app.eventcategory

import com.michibaum.chess_service.domain.EventCategory
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class EventCategoryService(
    private val eventCategoryRepository: EventCategoryRepository
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

}