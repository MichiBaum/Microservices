package com.michibaum.fitness_service.fitbit.api.weight

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FitbitWeightService(
    val fitbitWeightRepository: FitbitWeightRepository
) {

    @Transactional
    fun update(weights: List<FitbitWeight>) {
        fitbitWeightRepository.deleteAllByFitbitIdIn(weights.map { it.fitbitId })
        fitbitWeightRepository.saveAll(weights)
    }

}