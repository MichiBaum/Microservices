package com.michibaum.authentication_library

import com.auth0.jwt.JWT
import com.michibaum.permission_library.Permissions

class JwsWrapper(payload: String) {

    val jws = JWT.decode(payload)

    fun getUserId(): String {
        return jws
            .getClaim("userId")
            .asString()
    }

    fun getUsername(): String {
        return jws.subject
    }

    fun getPermissions(): List<Permissions> {
        return jws
            .getClaim("permissions")
            .asArray(String::class.java)
            .map { Permissions.valueOf(it) }
    }

}