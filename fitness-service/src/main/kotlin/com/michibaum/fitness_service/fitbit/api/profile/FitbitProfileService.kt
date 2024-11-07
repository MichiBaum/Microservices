package com.michibaum.fitness_service.fitbit.api.profile

import org.springframework.stereotype.Service

@Service
class FitbitProfileService(
    val fitbitProfileRepository: FitbitProfileRepository
) {

    fun update(profile: FitbitProfile) {
        val existingProfile = fitbitProfileRepository.findByUserId(profile.userId)
        if (existingProfile != null) {
            fitbitProfileRepository.delete(existingProfile)
        }

        fitbitProfileRepository.save(profile)
    }

}