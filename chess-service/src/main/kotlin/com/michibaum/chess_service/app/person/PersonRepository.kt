package com.michibaum.chess_service.app.person

import com.michibaum.chess_service.domain.Person
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PersonRepository: JpaRepository<Person, UUID> {
    fun findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(firstname: String, lastname: String): Set<Person>
    fun findByFirstnameContainingIgnoreCase(firstname: String): Set<Person>
    fun findByLastnameContainingIgnoreCase(lastname: String): Set<Person>
}