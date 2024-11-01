package com.michibaum.fitness_service.fitbit.oauth

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
class FitbitOAuthCredentials(
    @Column(nullable = false, unique = true)
    val accessToken: String,

    @Column(nullable = false, unique = false)
    val expiresIn: String,

    @Column(nullable = false, unique = true)
    val refreshToken: String,

    @Column(nullable = false, unique = false)
    val scope: String,

    @Column(nullable = false, unique = false)
    val fitbitUserId: String,

    @Column(nullable = false, unique = false)
    val userId: String,

    @Id
    @UuidGenerator
    val id: UUID = UUID.randomUUID(),
) {
}