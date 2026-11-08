package com.michibaum.poll_service.app.poll.dto

import com.michibaum.poll_service.database.QuestionType
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

data class CreateQuestionDto(
    @field:NotBlank
    val title: String,
    val description: String? = null,
    val type: QuestionType,
    val isRequired: Boolean = false,
    val orderIndex: Int = 0,
    val validator: ValidatorDto? = null,
    @field:Valid
    val options: List<CreateQuestionOptionDto> = emptyList()
)
