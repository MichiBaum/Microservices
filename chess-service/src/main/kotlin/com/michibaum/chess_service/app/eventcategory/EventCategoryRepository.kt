package com.michibaum.chess_service.app.eventcategory

import com.michibaum.chess_service.domain.EventCategory
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EventCategoryRepository: JpaRepository<EventCategory, UUID> {
}