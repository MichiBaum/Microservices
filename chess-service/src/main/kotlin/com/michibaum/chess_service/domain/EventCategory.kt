package com.michibaum.chess_service.domain

import jakarta.persistence.*
import java.util.*

@Entity
class EventCategory(
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val description: String,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}