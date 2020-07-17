package com.michibaum.lifemanagementbackend.user.service

import com.michibaum.lifemanagementbackend.user.domain.Permission
import com.michibaum.lifemanagementbackend.user.repository.PermissionRepository
import org.springframework.stereotype.Service

@Service
class PermissionService(
    private val permissionRepository: PermissionRepository
) {

    fun findById(id: Long): Permission? =
        permissionRepository.findById(id).orElseGet(null)

    fun findAll(): List<Permission> =
        permissionRepository.findAll()

}
