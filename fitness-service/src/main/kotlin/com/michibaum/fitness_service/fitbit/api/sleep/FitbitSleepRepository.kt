package com.michibaum.fitness_service.fitbit.api.sleep

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FitbitSleepRepository: JpaRepository<FitbitSleep, UUID> {
    fun deleteAllByFitbitIdIn(fitbitIds: List<Long>)
}