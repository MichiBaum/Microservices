package com.michibaum.lifemanagementbackend.core.exceptionHandler

import io.swagger.v3.oas.annotations.media.Schema


open class ErrorDetails(

    @field:Schema(
        example = "1596015112085",
        required = true,
        description = "The timestamp when the exception happenend"
    )
    open val timestamp: Long,

    @field:Schema(
        example =
        "Validation failed for argument [0] in public com.michibaum.lifemanagementbackend.user.dtos.ReturnUserDto" +
                "com.michibaum.lifemanagementbackend.user.controller.user.UserRestController.change(com.michibaum.lifemanagementbackend.user.dtos.UpdateUserDto,com.michibaum.lifemanagementbackend.user.domain.User): " +
                "[Field error in object 'updateUserDto' on field 'emailAddress': rejected value []; codes [NotBlank.updateUserDto.emailAddress,NotBlank.emailAddress,NotBlank.java.lang.String,NotBlank]; " +
                "arguments [org.springframework.context.support.DefaultMessageSourceResolvable: " +
                "codes [updateUserDto.emailAddress,emailAddress]; " +
                "arguments []; " +
                "default message [emailAddress]]; " +
                "default message [validation.user.emailAddress.notEmpty]]",
        required = false,
        description = "The exception message. Not set in production mode."
    )
    open val message: String,

    @field:Schema(
        example = "org.springframework.web.bind.MethodArgumentNotValidException",
        required = false,
        description = "The exception class. Not set in production mode."
    )
    open val exceptionClass: Class<out Exception?>?,

    @field:Schema(
        example = "uri=/lifemanagement/api/users/1;client=0:0:0:0:0:0:0:1;user=admin",
        required = true,
        description = "The level of the exception"
    )
    open val details: String

)

class ValidationErrorDetails(

    @field:Schema(
        example = "1596015112085",
        required = true,
        description = "The timestamp when the exception happenend"
    )
    override val timestamp: Long,

    @field:Schema(
        example =
        "Validation failed for argument [0] in public com.michibaum.lifemanagementbackend.user.dtos.ReturnUserDto" +
                "com.michibaum.lifemanagementbackend.user.controller.user.UserRestController.change(com.michibaum.lifemanagementbackend.user.dtos.UpdateUserDto,com.michibaum.lifemanagementbackend.user.domain.User): " +
                "[Field error in object 'updateUserDto' on field 'emailAddress': rejected value []; codes [NotBlank.updateUserDto.emailAddress,NotBlank.emailAddress,NotBlank.java.lang.String,NotBlank]; " +
                "arguments [org.springframework.context.support.DefaultMessageSourceResolvable: " +
                "codes [updateUserDto.emailAddress,emailAddress]; " +
                "arguments []; " +
                "default message [emailAddress]]; " +
                "default message [validation.user.emailAddress.notEmpty]]",
        required = false,
        description = "The exception message. Not set in production mode."
    )
    override val message: String,

    @field:Schema(
        example = "[validation.user.emailAddress.notEmpty, validation.user.emailAddress.notYEmail]",
        required = true,
        description = "The validation errors."
    )
    val validationErrors: List<String?>,

    @field:Schema(
        example = "org.springframework.web.bind.MethodArgumentNotValidException",
        required = false,
        description = "The exception class. Not set in production mode."
    )
    override val exceptionClass: Class<out Exception?>?,

    @field:Schema(
        example = "uri=/lifemanagement/api/users/1;client=0:0:0:0:0:0:0:1;user=admin",
        required = true,
        description = "The level of the exception"
    )
    override val details: String

) : ErrorDetails(timestamp, message, exceptionClass, details)
