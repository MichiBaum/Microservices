package com.michibaum.authentication_library

import com.auth0.jwt.exceptions.AlgorithmMismatchException
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT

sealed class JwsValidationResult

class JwsValidationSuccess(val jwt: DecodedJWT? = null) : JwsValidationResult()

open class JwsValidationFailed(val exception: Exception) : JwsValidationResult()
class JwsValidationMissing(ex: Exception) : JwsValidationFailed(ex)
class JwsValidationAlgorithmMismatch(ex: AlgorithmMismatchException) : JwsValidationFailed(ex)
class JwsValidationSignatureVerification(ex: SignatureVerificationException) : JwsValidationFailed(ex)
class JwsValidationTokenExpired(ex: TokenExpiredException) : JwsValidationFailed(ex)