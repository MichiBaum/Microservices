package com.michibaum.chess.app

import com.michibaum.chess.domain.Person
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PersonRepository: JpaRepository<Person, UUID> {
}