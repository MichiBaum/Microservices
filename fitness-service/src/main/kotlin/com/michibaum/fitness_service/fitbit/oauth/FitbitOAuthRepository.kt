package com.michibaum.fitness_service.fitbit.oauth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FitbitOAuthRepository: JpaRepository<FitbitOAuthData, String> {
    fun findByState(state: String): FitbitOAuthData?
}