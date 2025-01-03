package com.michibaum.usermanagement_service

import jakarta.persistence.*
import jakarta.persistence.FetchType.EAGER
import java.util.*

@Entity
class User (
    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @ManyToMany(fetch = EAGER, targetEntity = Permission::class)
    @JoinTable(name = "USER_PERMISSION_MAPPING", joinColumns = [JoinColumn(name = "user_id")], inverseJoinColumns = [JoinColumn(name = "permission_id")])
    val permissions: Set<Permission>,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)