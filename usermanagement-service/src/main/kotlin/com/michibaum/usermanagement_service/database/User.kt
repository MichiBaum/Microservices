package com.michibaum.usermanagement_service.database

import jakarta.persistence.*
import java.util.*

@Entity
class User (
    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)
