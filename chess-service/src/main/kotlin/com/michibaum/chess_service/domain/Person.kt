package com.michibaum.chess_service.domain

import jakarta.persistence.*
import java.util.*

@Entity
class Person(
    @Column(nullable = false)
    val firstname: String,

    @Column(nullable = false)
    val lastname: String,

    @OneToMany(mappedBy="person", fetch = FetchType.LAZY, targetEntity = Account::class)
    val accounts: Set<Account>,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
)
