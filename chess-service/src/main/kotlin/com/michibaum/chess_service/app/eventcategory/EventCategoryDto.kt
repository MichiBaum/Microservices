package com.michibaum.chess_service.app.eventcategory

import java.util.*

data class EventCategoryDto(
    val id: UUID,
    val title: String,
    val description: String,
)