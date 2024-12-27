package com.michibaum.authentication_library

import com.michibaum.permission_library.Permissions
import org.springframework.security.core.Authentication

fun Authentication.anyOf(vararg permissions: Permissions): Boolean {
    if (permissions.isEmpty())
        return true

    return permissions.any { permission ->
        authorities.any {
                a -> a.authority == permission.name
        }
    }
}
