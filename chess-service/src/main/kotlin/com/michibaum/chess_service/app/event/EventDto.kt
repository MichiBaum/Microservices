package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.app.eventcategory.EventCategoryDto
import com.michibaum.chess_service.app.person.PersonDto
import com.michibaum.chess_service.domain.ChessPlatform
import java.util.*

data class EventDto(
    val id: UUID,
    val title: String,
    val location: String?,
    val url: String? = null,
    val embedUrl: String? = null,
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val internalComment: String = "",
    val platform: ChessPlatform,
    val categories: List<EventCategoryDto> = mutableListOf(),
)