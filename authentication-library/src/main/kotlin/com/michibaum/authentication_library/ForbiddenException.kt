package com.michibaum.authentication_library

class ForbiddenException: RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
}