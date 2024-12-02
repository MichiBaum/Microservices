package com.michibaum.fitness_service.fitbit.api.sleep

import com.michibaum.fitness_service.api.sleep.Sleep
import com.michibaum.fitness_service.api.sleep.SleepStage
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import java.time.LocalDateTime

@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
class FitbitSleep(
    startTime: LocalDateTime,
    endTime: LocalDateTime,
    duration: Long,
    stages: Set<SleepStage>,
    userId: String,

    @Column(nullable = false, unique = true)
    val fitbitId: Long,

    ): Sleep(
    startTime = startTime,
    endTime = endTime,
    duration = duration,
    stages = stages,
    userId = userId

)