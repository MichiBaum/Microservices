package com.michibaum.usermanagement_service

import jakarta.persistence.*

@Entity
class Permission(
    @Id
    @Column(nullable = false, unique = true)
    val permission: String,

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = User::class)
    @JoinTable(name = "USER_PERMISSION_MAPPING", joinColumns = [JoinColumn(name = "permission_id")], inverseJoinColumns = [JoinColumn(name = "user_id")])
    val users: Set<User>
)