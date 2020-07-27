package com.michibaum.lifemanagementbackend.user.controller

import com.michibaum.lifemanagementbackend.core.argumentresolver.ArgumentResolver
import com.michibaum.lifemanagementbackend.core.exceptionHandler.ErrorDetails
import com.michibaum.lifemanagementbackend.user.converter.toDto
import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.user.dtos.ReturnUserDto
import com.michibaum.lifemanagementbackend.user.dtos.UpdateUserDto
import com.michibaum.lifemanagementbackend.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@Tag(name = "User Endpoints", description = "")
class UserRestController(
    private val userService: UserService
) {

    @Operation(summary = "Returns all user", description = "Returns all users as DTO", security = [ SecurityRequirement(name = "USER_MANAGEMENT") ])
    @ApiResponses(value = [
        ApiResponse( responseCode = "200", description = ""),
        ApiResponse( responseCode = "403", description = "Access denied", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    @PreAuthorize("hasAuthority('USER_MANAGEMENT')")
    @RequestMapping(value = ["/lifemanagement/api/users"], method = [RequestMethod.GET], produces = ["application/json" ], consumes = ["application/json" ])
    fun allUsers(): List<ReturnUserDto> =
        userService.findAll()
            .map(User::toDto)

    @Operation(summary = "Returns your user", description = "Returns your login user as DTO")
    @ApiResponses(value = [
        ApiResponse( responseCode = "200", description = ""),
        ApiResponse( responseCode = "403", description = "Access denied", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    @RequestMapping(value = ["/lifemanagement/api/users/me"], method = [RequestMethod.GET], produces = ["application/json" ], consumes = ["application/json" ])
    fun myUsers(
        @Parameter(description = "The current user, autoresolved through @ArgumentResolver", hidden = true) @ArgumentResolver currentUser: User
    ): ReturnUserDto =
        currentUser.toDto()

    @Operation(summary = "Updates an User", description = "Updates the user with the id in the url")
    @ApiResponses(value = [
        ApiResponse( responseCode = "200", description = ""),
        ApiResponse( responseCode = "403", description = "Access denied", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    @RequestMapping(value = ["/lifemanagement/api/users/{id}"], method = [RequestMethod.POST], produces = ["application/json" ], consumes = ["application/json" ])
    fun change(
        @Parameter(description = "The data to update the user") @Valid @RequestBody userDto: UpdateUserDto,
        @Parameter(description = "The user is resolved by the url variable", hidden = true) @PathVariable(name = "id") user: User
    ) =
        userService.update(user, userDto)
            .let(userService::save)
            .toDto()

    @Operation(summary = "Changes permission of an user", description = "Change permission of an user", security = [ SecurityRequirement(name = "USER_MANAGEMENT") ])
    @ApiResponses(value = [
        ApiResponse( responseCode = "200", description = ""),
        ApiResponse( responseCode = "403", description = "Access denied", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    @PreAuthorize("hasAuthority('USER_MANAGEMENT')")
    @RequestMapping(value = ["/lifemanagement/api/users/{id}/permissions"], method = [RequestMethod.POST], produces = ["application/json" ], consumes = ["application/json" ])
    fun changePermissions(
        @Parameter(description = "All the permissions the User should have") @RequestBody permissionIds: List<Long>,
        @Parameter(description = "The user is resolved by the url variable", hidden = true) @PathVariable(name = "id") user: User
    ): ReturnUserDto =
        userService.changePermissions(user, permissionIds)
            .let(userService::save)
            .toDto()

    //TODO create user endpoint
}
