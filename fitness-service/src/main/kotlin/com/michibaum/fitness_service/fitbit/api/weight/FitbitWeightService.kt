package com.michibaum.fitness_service.fitbit.api.weight

import org.springframework.stereotype.Service

@Service
class FitbitWeightService(
    val fitbitWeightRepository: FitbitWeightRepository
) {

    fun update(weight: List<FitbitWeight>) {
        fitbitWeightRepository.saveAll(weight)
    }

}