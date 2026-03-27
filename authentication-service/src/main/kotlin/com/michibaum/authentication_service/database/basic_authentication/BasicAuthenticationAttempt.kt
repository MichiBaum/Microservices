package com.michibaum.authentication_service.database.basic_authentication

import com.michibaum.authentication_service.database.AuthenticationAttempt
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "basic_authentication_attempt")
open class BasicAuthenticationAttempt(
    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "authentication_attempt_id", nullable = false)
    val authenticationAttempt: AuthenticationAttempt,

    @Column(name = "user_name_input", nullable = false)
    val username: String,

    @Id
    val id: UUID? = null
)