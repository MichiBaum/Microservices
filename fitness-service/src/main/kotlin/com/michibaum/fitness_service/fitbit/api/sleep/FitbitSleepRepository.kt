package com.michibaum.fitness_service.fitbit.api.sleep

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FitbitSleepRepository: JpaRepository<FitbitSleep, UUID> {
    fun deleteAllByFitbitIdIn(fitbitIds: List<Long>)
}