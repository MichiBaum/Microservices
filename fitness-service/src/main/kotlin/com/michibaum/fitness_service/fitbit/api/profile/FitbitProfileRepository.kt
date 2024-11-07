package com.michibaum.fitness_service.fitbit.api.profile

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FitbitProfileRepository: JpaRepository<FitbitProfile, UUID> {
    fun findByUserId(userId: String): FitbitProfile?
}