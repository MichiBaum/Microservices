package com.michibaum.authentication_library.public_endpoints

/**
 * Annotation to mark endpoints as public (no authentication required).
 * @param pattern The pattern type to use for matching the endpoint. Default is ANT.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PublicEndpoint(
    val pattern: PublicPattern = PublicPattern.ANT
)
