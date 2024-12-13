package com.michibaum.chess_service.app.eventcategory

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class WriteEventCategoryDto(
    @NotNull
    @NotBlank
    val title: String,
    @NotNull
    @NotBlank
    val description: String
)