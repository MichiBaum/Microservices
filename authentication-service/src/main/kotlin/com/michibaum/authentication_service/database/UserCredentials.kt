package com.michibaum.authentication_service.database

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "user_credentials")
class UserCredentials(
    @Column(nullable = false, name = "user_id")
    val userId: String,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false, name = "password_hash")
    var passwordHash: String,

    @Column(nullable = false)
    var enabled: Boolean = true,

    @Column(nullable = false, name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
)
