package com.michibaum.lifemanagementbackend.publicendpoint

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class PublicEndpoint(
    val numerus: Numerus = Numerus.NONE,
    val character: Character = Character.NONE
)
