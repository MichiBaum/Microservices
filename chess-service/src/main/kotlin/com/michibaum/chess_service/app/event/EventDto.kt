package com.michibaum.chess_service.app.event

data class EventDto(
    val id: String? = null,
    val title: String,
    val url: String? = null,
    val embedUrl: String? = null,
    val dateFrom: String? = null,
    val dateTo: String? = null,
)
