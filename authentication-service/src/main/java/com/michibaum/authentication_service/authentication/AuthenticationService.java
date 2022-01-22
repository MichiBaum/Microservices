package com.michibaum.authentication_service.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.michibaum.authentication_library.PublicKeyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final KeyPair keyPair;


    public PublicKeyDto getPublicKey() {
        return new PublicKeyDto(
                keyPair.getPublic().getAlgorithm(),
                keyPair.getPublic().getEncoded()
        );
    }

    public String generateJWS() {
        try {
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
            return JWT.create()
                    .withHeader(jwsHeaders())
                    .withIssuer("authentication-service")
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
        }
        return null;
    }

    private Map<String, Object> jwsHeaders(){
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("alg", keyPair.getPublic().getAlgorithm());
        return headers;
    }

}
