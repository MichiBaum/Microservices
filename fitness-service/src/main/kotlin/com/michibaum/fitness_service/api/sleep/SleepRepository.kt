package com.michibaum.fitness_service.api.sleep

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query
import java.util.*

@Repository
interface SleepRepository: JpaRepository<Sleep, UUID> {
    
    @Query("SELECT s FROM Sleep s WHERE s.userId = :userId")
    fun findByUserId(userId: String): Set<Sleep>
    
}