package com.michibaum.fitness_service.fitbit.oauth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FitbitOAuthRepository: JpaRepository<FitbitOAuthData, UUID> {
    fun findByState(state: String): FitbitOAuthData?
}