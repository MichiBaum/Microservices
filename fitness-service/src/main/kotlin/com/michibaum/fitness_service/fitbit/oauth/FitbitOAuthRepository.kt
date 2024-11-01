package com.michibaum.fitness_service.fitbit.oauth

import org.springframework.data.jpa.repository.JpaRepository

interface FitbitOAuthRepository: JpaRepository<FitbitOAuthData, String> {
    fun findByState(state: String): FitbitOAuthData?
}