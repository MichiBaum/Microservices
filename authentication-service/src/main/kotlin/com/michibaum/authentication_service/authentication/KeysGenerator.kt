package com.michibaum.authentication_service.authentication

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.*

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
}