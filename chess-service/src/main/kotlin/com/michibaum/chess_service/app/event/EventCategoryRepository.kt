package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.domain.EventCategory
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EventCategoryRepository: JpaRepository<EventCategory, UUID> {
}