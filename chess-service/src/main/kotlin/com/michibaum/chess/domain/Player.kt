package com.michibaum.chess.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
data class Player(
    @Id
    @UuidGenerator
    val id: UUID = UUID.randomUUID(),



)