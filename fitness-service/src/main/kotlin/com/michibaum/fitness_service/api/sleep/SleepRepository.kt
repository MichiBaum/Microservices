package com.michibaum.fitness_service.api.sleep

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SleepRepository: JpaRepository<Sleep, UUID> {
    fun findByUserId(userId: String): Set<Sleep>
}