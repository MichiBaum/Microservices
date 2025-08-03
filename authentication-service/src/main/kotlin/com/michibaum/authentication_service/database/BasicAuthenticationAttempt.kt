package com.michibaum.authentication_service.database

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name="basic_authentication_attempt")
@Inheritance(strategy = InheritanceType.JOINED)
open class BasicAuthenticationAttempt(

    @Column(name = "user_name", nullable = false)
    open val username: String,

    id: UUID? = null,
    date: LocalDateTime = LocalDateTime.now()
    
): AuthenticationAttempt(id = id, date = date) {}