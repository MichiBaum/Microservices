package com.michibaum.lifemanagementbackend

import com.michibaum.lifemanagementbackend.domain.Permission
import com.michibaum.lifemanagementbackend.domain.PermissionName
import com.michibaum.lifemanagementbackend.domain.User
import com.michibaum.lifemanagementbackend.repository.PermissionRepository
import com.michibaum.lifemanagementbackend.services.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class DbInitializer(
    private val userService: UserService,
    private val permissionRepository: PermissionRepository,
    bcryptPasswordEncoder: BCryptPasswordEncoder
    ) {

    @Value("\${application.system.environment}")
    private val systemEnvironment: String = ""

    private val savePermission: (p: PermissionName) -> Permission = { permissionRepository.findByName(it) ?: permissionRepository.save(Permission(it)) }
    private val saveUser: (User) -> User = { userService.findByName(it.name) ?: userService.save(it) }

    @PostConstruct
    private fun initialize() {
        when (systemEnvironment) {
            "dev" -> createDevDb()
            "prod" -> TODO("Not implemented yet")
        }
    }

    private var user_management: Permission = PermissionName.USER_MANAGEMENT.let(savePermission)
    private var see_logs: Permission = PermissionName.SEE_LOGS.let(savePermission)
    private var adminPermission: Permission = PermissionName.ADMIN.let(savePermission)

    private fun createDevDb() {
        admin = admin.let(saveUser)
        user = user.let(saveUser)
    }

    private var admin: User = User("admin", "admin@admin.com", bcryptPasswordEncoder.encode("admin"), true, mutableListOf(user_management, see_logs, adminPermission))
    private var user: User = User("user", "user@user.com", bcryptPasswordEncoder.encode("user"), true, mutableListOf())

}
