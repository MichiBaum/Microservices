package com.michibaum.lifemanagementbackend.user.controller.user

import com.michibaum.lifemanagementbackend.core.exceptionHandler.ErrorDetails
import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.user.dtos.ReturnUserDto
import com.michibaum.lifemanagementbackend.user.dtos.UpdateUserDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "User Endpoints", description = "All api endpoints which handle something with users")
interface UserRestControllerDocs{

    @SecurityRequirement(name = "Barear Token")
    @Operation(summary = "Returns all user", description = "Returns all users as DTO", security = [ SecurityRequirement(name = "USER_MANAGEMENT") ])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "OK: Request successfull completed"),
        ApiResponse(responseCode = "403", description = "Access denied: If an user is not authenticated, token expired, doesnt have the required permissions", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ]),
        ApiResponse(responseCode = "500", description = "Internal Server Error: If something internal broke accidentally", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    fun allUsers(): List<ReturnUserDto>



    @SecurityRequirement(name = "Barear Token")
    @Operation(summary = "Returns your user", description = "Returns your login user as DTO")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "OK: Request successfull completed"),
        ApiResponse(responseCode = "403", description = "Access denied: If an user is not authenticated, token expired, doesnt have the required permissions", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ]),
        ApiResponse(responseCode = "500", description = "Internal Server Error: If something internal broke accidentally", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    fun myUser(

        @Parameter(description = "The current user, autoresolved through @ArgumentResolver", hidden = true, required = true)
        currentUser: User

    ): ReturnUserDto



    @SecurityRequirement(name = "Barear Token")
    @Operation(summary = "Updates an User", description = "Updates the user with the id in the url")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "OK: Request successfull completed"),
        ApiResponse(responseCode = "403", description = "Access denied: If an user is not authenticated, token expired, doesnt have the required permissions", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ]),
        ApiResponse(responseCode = "500", description = "Internal Server Error: If something internal broke accidentally", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    fun change(

        @Parameter(description = "The data to update the user", required = true)
        userDto: UpdateUserDto,

        @Parameter(description = "The user is resolved by the url variable", hidden = true, required = true)
        user: User

    ):ReturnUserDto



    @SecurityRequirement(name = "Barear Token")
    @Operation(summary = "Changes permission of an user", description = "Change permission of an user", security = [ SecurityRequirement(name = "USER_MANAGEMENT") ])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "OK: Request successfull completed"),
        ApiResponse(responseCode = "403", description = "Access denied: If an user is not authenticated, token expired, doesnt have the required permissions", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ]),
        ApiResponse(responseCode = "500", description = "Internal Server Error: If something internal broke accidentally", content = [
            Content(schema = Schema(implementation = ErrorDetails::class))
        ])
    ])
    fun changePermissions(

        @Parameter(description = "All the permissions the User should have", required = true)
        permissionIds: List<Long>,

        @Parameter(description = "The user is resolved by the url variable", hidden = true, required = true)
        user: User

    ): ReturnUserDto

    //TODO create user endpoint
}
