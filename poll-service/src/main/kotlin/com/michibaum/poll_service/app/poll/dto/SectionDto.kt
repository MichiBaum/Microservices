package com.michibaum.poll_service.app.poll.dto

import java.util.UUID

data class SectionDto(
    val id: UUID,
    val title: String,
    val description: String?,
    val orderIndex: Int,
    val questions: List<QuestionDto>
)
