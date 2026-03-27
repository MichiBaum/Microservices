package com.michibaum.authentication_service.database

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name="authentication_attempt")
open class AuthenticationAttempt(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    open val id: UUID? = null,

    @Column(nullable = false)
    open val date: LocalDateTime = LocalDateTime.now()
) {
}