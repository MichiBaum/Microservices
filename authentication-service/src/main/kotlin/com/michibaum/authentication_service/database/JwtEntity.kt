package com.michibaum.authentication_service.database

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name="jwt")
open class JwtEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    open val id: UUID? = null,

    @Column(name = "jwt", nullable = false, columnDefinition = "MEDIUMTEXT")
    val jwt: String,
) {
}