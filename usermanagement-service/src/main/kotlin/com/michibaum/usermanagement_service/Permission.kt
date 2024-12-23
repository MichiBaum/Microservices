package com.michibaum.usermanagement_service

import jakarta.persistence.*

@Entity
class Permission(
    @Id
    @Column(nullable = false, unique = true)
    val permission: String,
)