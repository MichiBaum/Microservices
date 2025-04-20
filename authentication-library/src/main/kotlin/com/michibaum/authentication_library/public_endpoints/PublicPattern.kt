package com.michibaum.authentication_library.public_endpoints

enum class PublicPattern(val replacement: String) {
    ANT("*"),
    UUID("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")
}