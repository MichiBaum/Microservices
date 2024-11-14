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
        // TODO does work with flush, but there is a risk of loosing data. Problem ist transaction and sequence of statements
        fitbitSleepRepository.flush()
        fitbitSleepRepository.saveAll(sleeps)
    }

}