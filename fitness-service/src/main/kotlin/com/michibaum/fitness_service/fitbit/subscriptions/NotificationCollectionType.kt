package com.michibaum.fitness_service.fitbit.subscriptions

enum class NotificationCollectionType(val fitbitStrings: String) {
    ACTIVITIES("activities"), // activities collection requires activity scope.
    BODY("body"), // body collection requires weight scope.
    FOODS("foods"), // foods collection requires nutrition scope.
    SLEEP("sleep"), // sleep collection requires sleep scope.
    USER_REVOKED_ACCESS("userRevokedAccess") // userRevokedAccess collection does not require any scopes.
}