package com.michibaum.usermanagement_service

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "user_permission_mapping")
class UserPermissionMapping(
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    val permission: Permission,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
)