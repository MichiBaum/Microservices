package com.michibaum.fitness_service.fitbit.api.profile

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FitbitProfileRepository: JpaRepository<FitbitProfile, UUID> {
    fun findByUserId(userId: String): FitbitProfile?
}