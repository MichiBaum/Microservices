package com.michibaum.usermanagement_service.database

import jakarta.persistence.*
import java.util.*

@Entity
class User (
    @Deprecated("Username is deprecated and should not be set")
    @Column(nullable = true, unique = true)
    val username: String? = null,

    @Column(nullable = false, unique = true)
    val email: String,

    @Deprecated("Password is deprecated and should not be set")
    @Column(nullable = true)
    val password: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)
