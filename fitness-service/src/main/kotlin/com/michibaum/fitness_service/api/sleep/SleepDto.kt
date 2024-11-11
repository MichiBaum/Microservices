package com.michibaum.fitness_service.api.sleep

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.*

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