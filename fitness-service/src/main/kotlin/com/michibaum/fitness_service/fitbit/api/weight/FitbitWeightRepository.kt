package com.michibaum.fitness_service.fitbit.api.weight

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FitbitWeightRepository: JpaRepository<FitbitWeight, UUID> {
    fun deleteAllByFitbitIdIn(fitbitIds: List<Long>)
}