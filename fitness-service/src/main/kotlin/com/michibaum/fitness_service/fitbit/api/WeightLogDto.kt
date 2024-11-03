package com.michibaum.fitness_service.fitbit.api

data class WeightLogDto(
    val weight: List<WeightDto>
)

data class WeightDto(
    val bmi: Double,
    val date: String,
    val logId: Long,
    val source: String,
    val time: String,
    val weight: Double,
)