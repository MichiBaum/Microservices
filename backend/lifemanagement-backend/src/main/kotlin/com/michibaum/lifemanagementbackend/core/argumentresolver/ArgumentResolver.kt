package com.michibaum.lifemanagementbackend.core.argumentresolver

/**
 * Lazy loaded fiels are not initialized
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ArgumentResolver
