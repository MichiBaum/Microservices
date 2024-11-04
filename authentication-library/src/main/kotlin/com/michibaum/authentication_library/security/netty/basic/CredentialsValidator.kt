package com.michibaum.authentication_library.security.netty.basic

fun interface CredentialsValidator {
    fun validate(basic: BasicAuthentication): Boolean
}