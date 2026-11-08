package com.michibaum.poll_service.app.exception

class PollNotFoundException(message: String = "Poll not found") : RuntimeException(message)

class PollForbiddenException(message: String = "You do not have permission to access or modify this poll") : RuntimeException(message)

class PollImmutableException(message: String = "Cannot modify a poll that already has submissions or cannot be edited in its current state") : RuntimeException(message)

class InvalidPollException(message: String) : RuntimeException(message)
