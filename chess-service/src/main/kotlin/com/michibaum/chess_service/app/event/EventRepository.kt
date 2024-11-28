package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.domain.Event
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

interface EventRepository: JpaRepository<Event, UUID> {
    fun findByDateFromGreaterThanAndDateToLessThan(recent: LocalDate, upcoming: LocalDate): List<Event> // TODO maybe OR????
}