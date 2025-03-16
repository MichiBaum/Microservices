package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.*

interface EventRepository: JpaRepository<Event, UUID>, JpaSpecificationExecutor<Event> {
    @Query(value = "select e from Event e left join fetch e.categories categories")
    fun findAllEagerCategories(): List<Event>

    @EntityGraph(attributePaths = ["categories"])
    fun findByDateFromGreaterThanAndDateToLessThan(recent: LocalDate, upcoming: LocalDate): List<Event> // TODO maybe OR????

    fun findByCategoriesId(id: UUID): Set<Event>

    @Query(value = "select e from Event e left join fetch e.categories where e.id = :id")
    fun findByIdEagerCategories(id: UUID): Event?

    @Query(value = "select e from Event e left join fetch e.participants where e.id = :id")
    fun findByIdEagerParticipants(id: UUID): Event?
}