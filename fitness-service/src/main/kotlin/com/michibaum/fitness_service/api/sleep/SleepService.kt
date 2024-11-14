package com.michibaum.fitness_service.api.sleep

import org.springframework.stereotype.Service

@Service
class SleepService(
    private val sleepRepository: SleepRepository
) {
    fun getByUser(userId: String): Set<Sleep> {
        return sleepRepository.findByUserId(userId)
    }
}