package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name="person")
class Person(
    @Column(nullable = false)
    val firstname: String,

    @Column(nullable = false)
    val lastname: String,

    @Column(nullable = true)
    val federation: String?,

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    val birthday: LocalDate?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val gender: Gender,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}
