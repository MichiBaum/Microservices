package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.*

interface EventParticipantMappingRepository: JpaRepository<EventParticipantsMapping, EventParticipantsMappingId>, JpaSpecificationExecutor<Event> {
    
    @Modifying
    @Query("DELETE FROM EventParticipantsMapping epm WHERE epm.event = :event")
    fun deleteByEvent(event: Event)

}