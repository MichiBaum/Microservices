package com.michibaum.fitness_service.api.sleep

import java.time.LocalDateTime

data class SleepDto(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val duration: Long,
    val stages: List<SleepStageDto>,
)

data class SleepStageDto(
    val start: LocalDateTime,
    val end: LocalDateTime,
    val stage: Stage,
    val duration: Long,
)