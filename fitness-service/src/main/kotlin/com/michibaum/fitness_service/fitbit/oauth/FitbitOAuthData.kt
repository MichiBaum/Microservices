package com.michibaum.fitness_service.fitbit.oauth

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
class FitbitOAuthData(

    @Column(nullable = false, unique = true)
    val codeVerifier: String,

    @Column(nullable = false, unique = true)
    val codeChallenge: String,

    @Column(nullable = false, unique = true)
    val state: String,

    @Column(nullable = false, unique = false)
    val userId: String,

    @Id
    @UuidGenerator
    val id: UUID= UUID.randomUUID(),
) {
}