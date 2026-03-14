package com.michibaum.fitness_service.api.sleep

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SleepStageRepository: JpaRepository<SleepStage, UUID> {

    @Query("SELECT s FROM SleepStage s WHERE s.sleep.id = :sleepId")
    fun findBySleep(sleepId: UUID): Set<SleepStage>
    
}