package com.michibaum.lifemanagementbackend.user.controller.permission

import com.michibaum.lifemanagementbackend.core.exceptionHandler.ErrorDetails
import com.michibaum.lifemanagementbackend.user.dtos.ReturnPermissionDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Permission Endpoints", description = "All api endpoints which handle something with permissions")
interface PermissionRestControllerDocs {


    @SecurityRequirement(name = "Barear Token")
    @Operation(summary = "Returns all permissions", description = "Returns all permissions as DTO", security = [
        SecurityRequirement(name = "USER_MANAGEMENT")
    ])
    @ApiResponses(value = [
        ApiResponse( responseCode = "200", description = ""),
        ApiResponse( responseCode = "403", description = "Access denied", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    fun allPermissions(): List<ReturnPermissionDto>


}
