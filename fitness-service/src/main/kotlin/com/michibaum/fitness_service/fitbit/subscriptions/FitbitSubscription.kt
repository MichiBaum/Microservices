package com.michibaum.fitness_service.fitbit.subscriptions

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
class FitbitSubscription(

    @Column(nullable = false, unique = false)
    val verificationCode: String,

    @Column(nullable = false, unique = false)
    val userId: String,

    @Id
    @UuidGenerator
    val id: UUID = UUID.randomUUID(),
) {
}