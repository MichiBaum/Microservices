package com.michibaum.poll_service.app.poll.dto

import jakarta.validation.constraints.NotBlank

data class CreateQuestionOptionDto(
    @field:NotBlank
    val title: String,
    val description: String? = null,
    val orderIndex: Int = 0
)
