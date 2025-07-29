package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PersonRepository: JpaRepository<Person, UUID> {
    fun findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(firstname: String, lastname: String): Set<Person>
    fun findByFirstnameContainingIgnoreCase(firstname: String): Set<Person>
    fun findByLastnameContainingIgnoreCase(lastname: String): Set<Person>

    @Query("SELECT p FROM Person p JOIN FETCH p.accounts")
    fun findAllEagerAccounts(): List<Person>
}