package com.michibaum.lifemanagementbackend.permission.service

import com.michibaum.lifemanagementbackend.permission.domain.Permission
import com.michibaum.lifemanagementbackend.permission.repository.PermissionRepository
import org.springframework.stereotype.Service

@Service
class PermissionService(
    private val permissionRepository: PermissionRepository
) {

    fun findById(id: Long): Permission? =
        permissionRepository.findById(id).orElseGet(null)

}
