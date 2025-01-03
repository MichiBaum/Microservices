package com.michibaum.usermanagement_service

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class Permission(
    @Id
    @Column(nullable = false, unique = true)
    val permission: String,
)