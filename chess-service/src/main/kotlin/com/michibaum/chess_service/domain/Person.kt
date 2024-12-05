package com.michibaum.chess_service.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
class Person(
    @Column(nullable = false)
    val firstname: String,

    @Column(nullable = false)
    val lastname: String,

    @Column(nullable = true)
    val fideId: String?,

    @Column(nullable = true)
    val federation: String?,

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    val birthDate: LocalDate?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val gender: Gender,

    @OneToMany(mappedBy="person", fetch = FetchType.LAZY, targetEntity = Account::class)
    val accounts: Set<Account>,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
)
