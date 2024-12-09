package com.michibaum.chess_service.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@EntityListeners
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
    val birthday: LocalDate?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val gender: Gender,

    @OneToMany(mappedBy="person", fetch = FetchType.EAGER, targetEntity = Account::class) // Most searches are for persons. Person in Account can be lazy. If searched for a account another request is used to get the person.
    val accounts: Set<Account>,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
)
