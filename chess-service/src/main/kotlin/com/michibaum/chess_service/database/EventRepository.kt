package com.michibaum.chess_service.database

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface EventRepository: JpaRepository<Event, UUID>, JpaSpecificationExecutor<Event> {
    @EntityGraph(value = "with-categories")
    @Query(value = "select e from Event e left join fetch e.categories categories")
    fun findAllEagerCategories(): List<Event>

    @EntityGraph(value = "with-categories")
    fun findByDateToGreaterThanAndDateFromLessThan(recent: LocalDate, upcoming: LocalDate): List<Event>

    @EntityGraph(value = "with-categories")
    @Query(value = "select e from Event e left join fetch e.categories categories where e.id = :id")
    fun findByIdEagerCategories(id: UUID): Event?

    @EntityGraph(value = "with-participants-and-accounts")
    @Query(value = "select e from Event e left join fetch e.participants participants where e.id = :id")
    fun findByIdEagerParticipantsAndAccounts(id: UUID): Event?

    @EntityGraph(value = "with-categories")
    override fun findAll(spec: Specification<Event>, pageable: Pageable): Page<Event>
}