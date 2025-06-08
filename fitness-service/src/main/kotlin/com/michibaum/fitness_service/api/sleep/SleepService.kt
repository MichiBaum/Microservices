package com.michibaum.fitness_service.api.sleep

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SleepService(
    private val sleepRepository: SleepRepository,
    private val sleepStageRepository: SleepStageRepository
) {
    fun getByUser(userId: String): Set<Sleep> {
        return sleepRepository.findByUserId(userId)
    }

    fun getStagesBySleep(sleepId: UUID): Set<SleepStage> {
        return sleepStageRepository.findBySleep(sleepId)
    }
}