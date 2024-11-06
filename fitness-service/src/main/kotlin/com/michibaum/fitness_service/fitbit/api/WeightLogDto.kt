package com.michibaum.fitness_service.fitbit.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeightLogDto(
    val weight: List<WeightDto> = mutableListOf()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeightDto(
    val bmi: Double,
    val date: String,
    val fat: Int?,
    val logId: Long,
    val source: String,
    val time: String,
    val weight: Double,
)