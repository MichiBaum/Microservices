package com.michibaum.fitness_service.api.profile

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
open class Profile (

    @Column(nullable = false, unique = false)
    val age: String,
    @Column(nullable = false, unique = false)
    val country: String,
    @Column(nullable = false, unique = false)
    val fullName: String,
    @Column(nullable = false, unique = false)
    val gender: String,
    @Column(nullable = false, unique = false)
    val height: Double,

    @Column(nullable = false, unique = false)
    val userId: String,

    @Id
    @UuidGenerator
    val id: UUID = UUID.randomUUID(),
){
}