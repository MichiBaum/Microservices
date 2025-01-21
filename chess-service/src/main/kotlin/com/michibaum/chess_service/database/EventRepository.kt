package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.time.LocalDate
import java.util.*

interface EventRepository: JpaRepository<Event, UUID>, JpaSpecificationExecutor<Event> {
    fun findByDateFromGreaterThanAndDateToLessThan(recent: LocalDate, upcoming: LocalDate): List<Event> // TODO maybe OR????
    fun findByCategoriesId(id: UUID): Set<Event>
}