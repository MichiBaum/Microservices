package com.michibaum.permission_library

fun <T : Enum<T>> createPermissionString(enum: T) = enum.javaClass.name + enum.name

interface PermissionUtil {

    fun toPermissionString(): String

}
