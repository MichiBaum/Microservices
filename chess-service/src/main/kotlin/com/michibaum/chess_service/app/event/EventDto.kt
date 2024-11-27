package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.domain.EventCategory

data class EventDto(
    val id: String? = null,
    val title: String,
    val url: String? = null,
    val embedUrl: String? = null,
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val categories: List<EventCategoryDto> = mutableListOf(),
)

data class EventCategoryDto(
    val id: String,
    val title: String,
    val description: String,
)