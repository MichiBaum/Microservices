package com.michibaum.usermanagement_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "user")
class User (
    @Column(nullable = false, unique = true)
    val email: String,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)
