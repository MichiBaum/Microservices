package com.michibaum.poll_service.app.poll.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

data class CreateSectionDto(
    @field:NotBlank
    val title: String,
    val description: String? = null,
    val orderIndex: Int = 0,
    @field:Valid
    val questions: List<CreateQuestionDto> = emptyList()
)
