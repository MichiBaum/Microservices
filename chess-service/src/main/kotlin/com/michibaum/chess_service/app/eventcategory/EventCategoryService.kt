package com.michibaum.chess_service.app.eventcategory

import com.michibaum.chess_service.domain.EventCategory
import org.springframework.stereotype.Service
import java.util.*

@Service
class EventCategoryService(
    private val eventCategoryRepository: EventCategoryRepository
) {

    fun getAll(): List<EventCategory> =
        eventCategoryRepository.findAll()

    fun findAllById(categoryIds: List<UUID>): List<EventCategory> =
        eventCategoryRepository.findAllById(categoryIds)

}