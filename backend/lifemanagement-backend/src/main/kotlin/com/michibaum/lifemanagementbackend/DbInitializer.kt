package com.michibaum.lifemanagementbackend

import com.michibaum.lifemanagementbackend.domain.Permission
import com.michibaum.lifemanagementbackend.domain.PermissionName
import com.michibaum.lifemanagementbackend.domain.User
import com.michibaum.lifemanagementbackend.repository.PermissionRepository
import com.michibaum.lifemanagementbackend.service.UserService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct

@Component
class DbInitializer(
    private val userService: UserService,
    private val permissionRepository: PermissionRepository,
    bcryptPasswordEncoder: BCryptPasswordEncoder
) {

    private val logger = KotlinLogging.logger {}

    @Value("\${application.system.environment}")
    private val systemEnvironment: String = ""

    private val savePermission: (p: PermissionName) -> Permission = { permissionRepository.findByName(it) ?: permissionRepository.save( Permission(it) ) }
    private val saveUser: (User) -> User = { userService.findByName(it.name) ?: userService.save(it) }

    private val userPassword = generateRandomPassword()
    private val adminPassword = generateRandomPassword()

    @PostConstruct
    private fun initialize() {
        logger.info("user password is '$userPassword'")
        logger.info("admin password is '$adminPassword'")

        when (systemEnvironment) {
            "dev" -> {
                user = user.let(saveUser)
                admin = admin.let(saveUser)
            }
            "dev_h2" -> {
                user = user.let(saveUser)
                admin = admin.let(saveUser)
            }
            "docker" -> {
                admin = admin.let(saveUser)
            }
        }
    }

    private var user_management: Permission = PermissionName.USER_MANAGEMENT.let(savePermission)
    private var see_logs: Permission = PermissionName.SEE_LOGS.let(savePermission)
    private var adminPermission: Permission = PermissionName.ADMIN.let(savePermission)

    private var admin: User =
        User(
            "admin",
            "admin@admin.com",
            bcryptPasswordEncoder.encode(adminPassword),
            true,
            mutableListOf(user_management, see_logs, adminPermission)
        )

    private var user: User =
        User(
            "user",
            "user@user.com",
            bcryptPasswordEncoder.encode(userPassword),
            true
        )

    private fun generateRandomPassword(): String =
        Random().ints(48, 122 + 1) // numeral '0' and letter 'z'
            .filter { i: Int -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97) }
            .limit(10)
            .collect(
                { StringBuilder() },
                { obj: StringBuilder, codePoint: Int -> obj.appendCodePoint(codePoint) }
            ) {
                    obj: StringBuilder, s: StringBuilder? -> obj.append( s )
            }
            .toString()

}
