package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EventCategoryRepository: JpaRepository<EventCategory, UUID> {
}