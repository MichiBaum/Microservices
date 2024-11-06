package com.michibaum.fitness_service.api.weight

import java.time.LocalDateTime
import java.util.*

data class WeightDto(
    val weight: Double,
    val bmi: Double,
    val fatPercentage: Int?,
    val date: LocalDateTime,
    val id: UUID
)
