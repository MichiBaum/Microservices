package com.michibaum.fitness_service.fitbit.api.weight

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FitbitWeightRepository: JpaRepository<FitbitWeight, UUID> {
    fun deleteAllByFitbitIdIn(fitbitIds: List<Long>)
}