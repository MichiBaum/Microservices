package com.michibaum.lifemanagementbackend.user.controller

import com.michibaum.lifemanagementbackend.core.exceptionHandler.ErrorDetails
import com.michibaum.lifemanagementbackend.user.converter.toDto
import com.michibaum.lifemanagementbackend.user.domain.Permission
import com.michibaum.lifemanagementbackend.user.dtos.ReturnPermissionDto
import com.michibaum.lifemanagementbackend.user.service.PermissionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Encoding
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Permission Endpoints", description = "")
class PermissionRestController(
    private val permissionService: PermissionService
) {

    @Operation(summary = "Returns all permissions", description = "Returns all permissions as DTO", security = [
        SecurityRequirement(name = "USER_MANAGEMENT")
    ])
    @ApiResponses(value = [
        ApiResponse( responseCode = "200", description = ""),
        ApiResponse( responseCode = "403", description = "Access denied", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    @PreAuthorize("hasAuthority('USER_MANAGEMENT')")
    @RequestMapping(value = ["/lifemanagement/api/permissions"], method = [RequestMethod.GET], produces = ["application/json" ], consumes = ["application/json" ])
    fun allPermissions(): List<ReturnPermissionDto> =
        permissionService.findAll()
            .map(Permission::toDto)

}
