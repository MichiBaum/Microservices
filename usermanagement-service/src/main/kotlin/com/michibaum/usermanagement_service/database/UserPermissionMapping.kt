package com.michibaum.usermanagement_service.database

import jakarta.persistence.*
import java.io.Serializable
import java.util.UUID

@Entity
@Table(name = "user_permission_mapping")
@IdClass(UserPermissionMappingId::class)
class UserPermissionMapping(
    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    val permission: Permission
)

class UserPermissionMappingId(
    var user: UUID? = null,
    var permission: String? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserPermissionMappingId) return false

        if (user != other.user) return false
        if (permission != other.permission) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user?.hashCode() ?: 0
        result = 31 * result + (permission?.hashCode() ?: 0)
        return result
    }
}
