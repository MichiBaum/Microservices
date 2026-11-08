package com.michibaum.poll_service.app.poll.dto

import com.michibaum.poll_service.database.QuestionType
import java.util.UUID

data class QuestionDto(
    val id: UUID,
    val title: String,
    val description: String?,
    val type: QuestionType,
    val isRequired: Boolean,
    val orderIndex: Int,
    val validator: ValidatorDto?,
    val options: List<QuestionOptionDto>
)
