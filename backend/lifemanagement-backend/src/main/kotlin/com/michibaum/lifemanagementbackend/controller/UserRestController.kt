package com.michibaum.lifemanagementbackend.controller

import com.michibaum.lifemanagementbackend.argumentresolver.ArgumentResolver
import com.michibaum.lifemanagementbackend.converter.toDto
import com.michibaum.lifemanagementbackend.domain.User
import com.michibaum.lifemanagementbackend.dtos.ReturnUserDto
import com.michibaum.lifemanagementbackend.dtos.UpdateUserDto
import com.michibaum.lifemanagementbackend.service.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class UserRestController(
    private val userService: UserService
) {

    @PreAuthorize("hasAuthority('USER_MANAGEMENT')")
    @RequestMapping(value = ["/lifemanagement/api/users"], method = [RequestMethod.GET])
    fun allUsers(): List<ReturnUserDto> =
        userService.findAll()
            .map(User::toDto)

    @RequestMapping(value = ["/lifemanagement/api/users/me"], method = [RequestMethod.GET])
    fun myUsers(@ArgumentResolver currentUser: User): ReturnUserDto =
        currentUser.toDto()

    @RequestMapping(value = ["/lifemanagement/api/users/{id}"], method = [RequestMethod.POST])
    fun change(@RequestBody userDto: UpdateUserDto, @PathVariable(name = "id") user: User): ReturnUserDto =
        userService.updateUser(user, userDto)
            .let(userService::save)
            .toDto()
}
