package com.michibaum.lifemanagementbackend.core.security

internal object SecurityConstants {
    const val SECRET = "SecretKeyToGenJWTs"
    const val EXPIRATION_TIME: Long = 1000 * 3600 * 24 * 2 // 2 days
    const val TOKEN_PREFIX = "Bearer "
    const val HEADER_STRING = "Authorization"
}
