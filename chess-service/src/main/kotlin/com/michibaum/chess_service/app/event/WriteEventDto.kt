package com.michibaum.chess_service.app.event

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern

class WriteEventDto(
    @NotEmpty
    val title: String,

    val location: String?,

    @NotEmpty
    @Pattern(regexp = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])\$")
    val dateFrom: String,

    @NotEmpty
    @Pattern(regexp = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])\$")
    val dateTo: String,

    val url: String? = null,

    val embedUrl: String? = null,

    val categoryIds: List<String> = mutableListOf(),

    val participantsIds: List<String> = mutableListOf()
)
