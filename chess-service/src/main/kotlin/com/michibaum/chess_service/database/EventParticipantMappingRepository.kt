package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.*

interface EventParticipantMappingRepository: JpaRepository<EventParticipantsMapping, EventParticipantsMappingId>, JpaSpecificationExecutor<Event> {
   
}