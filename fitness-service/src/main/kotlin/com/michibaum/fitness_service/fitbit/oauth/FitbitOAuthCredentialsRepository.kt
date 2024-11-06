package com.michibaum.fitness_service.fitbit.oauth

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FitbitOAuthCredentialsRepository: JpaRepository<FitbitOAuthCredentials, UUID> {
    fun findByUserId(userId: String): FitbitOAuthCredentials?
}