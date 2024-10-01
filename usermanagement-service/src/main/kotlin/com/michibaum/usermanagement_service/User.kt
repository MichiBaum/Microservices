package com.michibaum.usermanagement_service

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
data class User (
    @Id
    @UuidGenerator
    val id: UUID,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String
) {
    constructor() : this(id = UUID.randomUUID(), username = "", email = "", password = "") {

    }
}