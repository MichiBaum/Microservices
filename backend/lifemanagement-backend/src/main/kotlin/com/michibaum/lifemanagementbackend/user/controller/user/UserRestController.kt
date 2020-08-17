package com.michibaum.lifemanagementbackend.user.controller.user

import com.michibaum.lifemanagementbackend.core.argumentresolver.ArgumentResolver
import com.michibaum.lifemanagementbackend.user.converter.toDto
import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.user.dtos.ReturnUserDto
import com.michibaum.lifemanagementbackend.user.dtos.UpdateUserDto
import com.michibaum.lifemanagementbackend.user.service.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class UserRestController(
    private val userService: UserService
): UserRestControllerDocs {

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasAuthority('USER_MANAGEMENT')")
    @RequestMapping(value = ["/lifemanagement/api/users"], method = [RequestMethod.GET], produces = ["application/json" ])
    override fun allUsers(): List<ReturnUserDto> =
        userService.findAll()
            .map(User::toDto)

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    @RequestMapping(value = ["/lifemanagement/api/users/me"], method = [RequestMethod.GET], produces = ["application/json" ])
    override fun myUser(@ArgumentResolver currentUser: User): ReturnUserDto =
        currentUser.toDto()

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    @RequestMapping(value = ["/lifemanagement/api/users/{id}"], method = [RequestMethod.POST], produces = ["application/json" ])
    override fun change(@Valid @RequestBody userDto: UpdateUserDto, @PathVariable(name = "id") user: User): ReturnUserDto =
        userService.update(user, userDto)
            .let(userService::save)
            .toDto()

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasAuthority('USER_MANAGEMENT')")
    @RequestMapping(value = ["/lifemanagement/api/users/{id}/permissions"], method = [RequestMethod.POST], produces = ["application/json" ])
    override fun changePermissions(@RequestBody permissionIds: List<Long>, @PathVariable(name = "id") user: User): ReturnUserDto =
        userService.changePermissions(user, permissionIds)
            .let(userService::save)
            .toDto()

    //TODO create user endpoint
}
