package com.michibaum.lifemanagementbackend.security

import com.michibaum.lifemanagementbackend.permission.domain.PermissionName
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityGuard {
    fun hasPermission(permission: PermissionName): Boolean =
        SecurityContextHolder
            .getContext()
            ?.authentication
            ?.authorities
            ?.any { it.authority == permission.name }
            ?: false
}
