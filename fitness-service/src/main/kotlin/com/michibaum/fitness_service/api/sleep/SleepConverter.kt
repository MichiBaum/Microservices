package com.michibaum.fitness_service.api.sleep

import org.springframework.stereotype.Component

@Component
class SleepConverter {
    fun toDto(sleep: Sleep): SleepDto {
        val stages = sleep.stages.map { toDto(it) }
        return SleepDto(
            startTime = sleep.startTime,
            endTime = sleep.endTime,
            duration = sleep.duration,
            stages = stages
        )
    }

    fun toDto(sleepStage: SleepStage): SleepStageDto{
        return SleepStageDto(
            start = sleepStage.start,
            end = sleepStage.end,
            stage = sleepStage.stage,
            duration = sleepStage.duration
        )
    }
}