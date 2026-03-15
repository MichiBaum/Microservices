package com.michibaum.chess_service.app.eventcategory

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class WriteEventCategoryDto(
    @field:NotNull
    @field:NotBlank
    val title: String,
    @field:NotNull
    @field:NotBlank
    val description: String
)