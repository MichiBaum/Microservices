package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface EventRepository: JpaRepository<Event, UUID>, JpaSpecificationExecutor<Event> {
   
    @Query(value = "select e from Event e")
    override fun findAll(): List<Event>

    @Query(value = "select e from Event e where e.dateFrom >= :recent and e.dateTo <= :upcoming")
    fun findByDateFromGreaterThanAndDateToLessThan(recent: LocalDate, upcoming: LocalDate): List<Event> // TODO maybe OR????

    @Query(value = "select distinct e from Event e join EventCategoryMapping ecm on e.id = ecm.event where ecm.category = :id")
    fun findByCategoryId(id: UUID): Set<Event>

    @Query(value = "select e from Event e  where e.id = :id")
    fun findByEventId(id: UUID): Event?

}