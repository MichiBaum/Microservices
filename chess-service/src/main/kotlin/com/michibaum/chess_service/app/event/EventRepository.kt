package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.domain.Event
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EventRepository: JpaRepository<Event, UUID> {
}