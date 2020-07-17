package com.michibaum.lifemanagementbackend.controller

import com.michibaum.lifemanagementbackend.converter.toDto
import com.michibaum.lifemanagementbackend.domain.Permission
import com.michibaum.lifemanagementbackend.dtos.ReturnPermissionDto
import com.michibaum.lifemanagementbackend.service.PermissionService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class PermissionRestController(
    private val permissionService: PermissionService
) {

    @PreAuthorize("hasAuthority('USER_MANAGEMENT')")
    @RequestMapping(value = ["/lifemanagement/api/permissions"], method = [RequestMethod.GET])
    fun allPermissions(): List<ReturnPermissionDto> =
        permissionService.findAll()
            .map(Permission::toDto)

}
