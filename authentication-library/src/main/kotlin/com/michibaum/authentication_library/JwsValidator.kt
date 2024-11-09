package com.michibaum.authentication_library

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.exceptions.*
import java.security.interfaces.RSAPublicKey

open class JwsValidator {
    fun validate(token: String, publicKey: RSAPublicKey?): JwsValidationResult {
        try {
            if (publicKey == null)
                return JwsValidationMissing(Exception("Public Key is missing"))
            val algorithm = JwsAlgorithm.algorithm(publicKey)
            val verifier: JWTVerifier = JWT.require(algorithm)
                .withIssuer("authentication-service")
                .build()
            val verifiedJwt = verifier.verify(token)
            return JwsValidationSuccess() // TODO JwsValidationSuccess(verifiedJwt)
        } catch (ex: AlgorithmMismatchException) {
            return JwsValidationAlgorithmMismatch(ex)
        } catch (ex: SignatureVerificationException) {
            return JwsValidationSignatureVerification(ex)
        } catch (ex: TokenExpiredException){
            return JwsValidationTokenExpired(ex)
        } catch (ex: MissingClaimException) {
            return JwsValidationFailed(ex)
        } catch (ex: IncorrectClaimException) {
            return JwsValidationFailed(ex)
        } catch (ex: JWTVerificationException) {
            return JwsValidationFailed(ex)
        } catch (ex: Exception) {
            return JwsValidationFailed(ex)
        }
    }

}