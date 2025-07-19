package com.michibaum.authentication_service.app.authentication

import com.auth0.jwt.algorithms.Algorithm
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@Configuration
class KeysGenerator {

    @Bean
    fun keyPair(): KeyPair {
        //Creating KeyPair generator object
        val keyPairGen: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")

        //Initializing the KeyPairGenerator
        keyPairGen.initialize(4096)

        //Generating the pair of keys
        return keyPairGen.generateKeyPair()
    }

    @Bean
    fun algorithm(keyPair: KeyPair): Algorithm {
        val publicKey: RSAPublicKey = keyPair.public as RSAPublicKey
        val privateKey: RSAPrivateKey = keyPair.private as RSAPrivateKey
        return Algorithm.RSA256(publicKey, privateKey)
    }

}