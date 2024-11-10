package com.michibaum.fitness_service.fitbit.api.sleep

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FitbitSleepService(
    val fitbitSleepRepository: FitbitSleepRepository
) {

    @Transactional
    fun update(sleeps: List<FitbitSleep>) {
        fitbitSleepRepository.deleteAllByFitbitIdIn(sleeps.map { it.fitbitId })
        fitbitSleepRepository.saveAll(sleeps)
    }

}