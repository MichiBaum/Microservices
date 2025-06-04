package com.michibaum.chess_service.database

import java.util.UUID

data class EventCategoryMappingProjection(
    val eventId: UUID,
    val categoryId: UUID
) {
}