package com.michibaum.authentication_library;

import org.springframework.web.bind.annotation.PostMapping;

public interface AuthenticationEndpoints {

    @PostMapping(value = "/checkPasword")
    PublicKeyDto getPublicKey();

}
