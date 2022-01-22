package com.michibaum.authentication_service.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.*;

@Configuration
public class KeysGenerator {

    @Bean
    KeyPair keyPair() throws NoSuchAlgorithmException {
        //Creating KeyPair generator object
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");

        //Initializing the KeyPairGenerator
        keyPairGen.initialize(4096);

        //Generating the pair of keys
        return keyPairGen.generateKeyPair();
    }


}
