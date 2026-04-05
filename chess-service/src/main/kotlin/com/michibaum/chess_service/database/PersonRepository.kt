package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PersonRepository: JpaRepository<Person, UUID> {
    @EntityGraph(value = "with-accounts")
    fun findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(firstname: String, lastname: String): Set<Person>

    @EntityGraph(value = "with-accounts")
    fun findByFirstnameContainingIgnoreCase(firstname: String): Set<Person>

    @EntityGraph(value = "with-accounts")
    fun findByLastnameContainingIgnoreCase(lastname: String): Set<Person>

    @Query("SELECT p FROM Person p LEFT JOIN FETCH p.accounts")
    fun findAllEagerAccounts(): List<Person>
}