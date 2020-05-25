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
    userService: UserService
) {

    private val getAllUsers: () -> List<User> = userService.findAll

    private val updateUser: (User, UpdateUserDto) -> User = userService.updateUser

    private val saveUser: (User) -> User = userService.save

    @PreAuthorize("hasAuthority('USER_MANAGEMENT')")
    @RequestMapping(value = ["/lifemanagement/api/users"], method = [RequestMethod.GET])
    fun allUsers(): List<ReturnUserDto> = getAllUsers().map(
        User::toDto)

    @RequestMapping(value = ["/lifemanagement/api/users/me"], method = [RequestMethod.GET])
    fun myUsers(@ArgumentResolver currentUser: User): ReturnUserDto = currentUser.toDto()

    @RequestMapping(value = ["/lifemanagement/api/users/{id}"], method = [RequestMethod.POST])
    fun change(@RequestBody userDto: UpdateUserDto, @PathVariable(name = "id") user: User): ReturnUserDto {
        return updateUser(user, userDto)
            .let(saveUser)
            .toDto()
    }
}
