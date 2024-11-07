package com.michibaum.fitness_service.fitbit.oauth

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FitbitOAuthCredentialsRepository: JpaRepository<FitbitOAuthCredentials, UUID> {
    fun findByUserIdAndDeactivatedFalse(userId: String): FitbitOAuthCredentials?
}