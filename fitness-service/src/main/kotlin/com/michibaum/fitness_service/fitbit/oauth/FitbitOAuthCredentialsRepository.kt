package com.michibaum.fitness_service.fitbit.oauth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FitbitOAuthCredentialsRepository: JpaRepository<FitbitOAuthCredentials, UUID> {
    fun findByUserIdAndDeactivatedFalse(userId: String): FitbitOAuthCredentials?
}