package com.michibaum.lifemanagementbackend

import com.michibaum.lifemanagementbackend.user.domain.Permission
import com.michibaum.lifemanagementbackend.user.domain.PermissionName
import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.user.repository.PermissionRepository
import com.michibaum.lifemanagementbackend.user.service.UserService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct

@Component
class DbInitializer(
    private val userService: UserService,
    private val permissionRepository: PermissionRepository,
    private val argon2PasswordEncoder: Argon2PasswordEncoder
) {

    private val logger = KotlinLogging.logger {}

    @Value("\${application.system.environment}")
    private val systemEnvironment: String = ""

    private val savePermission: (PermissionName) -> Permission = {
        permissionRepository.findByName(it) ?: permissionRepository.save(Permission(it))
    }

    private val changeUserPassword: (User, User) -> User = {
        existingUser, newUserWithPassword -> existingUser.also {
            it.password = newUserWithPassword.password
        }
    }

    private val saveUser: (User) -> User = {
        userService.findByName(it.name)?.let{
                existingUser -> userService.save(changeUserPassword(existingUser, it))
        } ?: userService.save(it)
    }

    private val userPassword = generateRandomPassword()
    private val adminPassword = generateRandomPassword()

    @PostConstruct
    private fun initialize() {

        when (systemEnvironment) {
            "dev" -> {
                logger.info("user password is '$userPassword'")
                logger.info("admin password is '$adminPassword'")
                user = user.let(saveUser)
                admin = admin.let(saveUser)
            }
            "dev_h2" -> {
                logger.info("user password is '$userPassword'")
                logger.info("admin password is '$adminPassword'")
                user = user.let(saveUser)
                admin = admin.let(saveUser)
            }
            "docker" -> {
                logger.info("admin password is '$adminPassword'")
                admin = admin.let(saveUser)
            }
        }
    }

    private var user_management: Permission = PermissionName.USER_MANAGEMENT.let(savePermission)
    private var see_logs: Permission = PermissionName.SEE_LOGS.let(savePermission)
    private var adminPermission: Permission = PermissionName.ADMIN.let(savePermission)
    private var developTools: Permission = PermissionName.DEVELOP_TOOLS.let(savePermission)

    private fun generateHashAndLogTime(password: String): String {
        val start = Date().time
        val hash = argon2PasswordEncoder.encode(password)
        val end = Date().time

        logger.info("Hashing password took ${end - start} milliseconds")

        return hash
    }

    private var admin: User =
        User(
            "admin",
            "admin@admin.com",
            generateHashAndLogTime(adminPassword),
            true,
            mutableListOf(user_management, see_logs, adminPermission, developTools)
        )

    private var user: User =
        User(
            "user",
            "user@user.com",
            generateHashAndLogTime(userPassword),
            true
        )

    private fun generateRandomPassword(): String =
        Random().ints(48, 122 + 1) // numeral '0' and letter 'z'
            .filter { i: Int -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97) }
            .limit(10)
            .collect(
                { StringBuilder() },
                { obj: StringBuilder, codePoint: Int -> obj.appendCodePoint(codePoint) }
            ) { obj: StringBuilder, s: StringBuilder? ->
                obj.append(s)
            }
            .toString()

}
