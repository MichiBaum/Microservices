package com.michibaum.authentication_library

import com.auth0.jwt.algorithms.Algorithm
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

class JwsAlgorithm {

    companion object {
        fun algorithm(publicKey: RSAPublicKey, privateKey: RSAPrivateKey): Algorithm {
            return Algorithm.RSA256(publicKey, privateKey)
        }

        fun algorithm(publicKey: RSAPublicKey): Algorithm {
            return Algorithm.RSA256(publicKey)
        }
    }

}