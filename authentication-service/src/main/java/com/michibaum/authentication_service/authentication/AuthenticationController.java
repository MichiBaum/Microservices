package com.michibaum.authentication_service.authentication;

import com.michibaum.authentication_library.AuthenticationEndpoints;
import com.michibaum.authentication_library.PublicKeyDto;
import com.michibaum.authentication_service.config.UsermanagementClient;
import com.michibaum.usermanagement_library.LoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class AuthenticationController implements AuthenticationEndpoints {

    private final AuthenticationService authenticationService;
    private final UsermanagementClient usermanagementClient;

    @PostMapping(value = "/authenticate")
    void authenticate(@RequestBody AuthenticationDto authenticationDto){

        LoginDto loginDto = new LoginDto(authenticationDto.getUsername(), authenticationDto.getPassword());
        Boolean passwordCorrect = usermanagementClient.checkPassword(loginDto);
        if(passwordCorrect) {
            authenticationService.generateJWS();
        }


    }

    @Override
    public PublicKeyDto getPublicKey() {
        return authenticationService.getPublicKey();
    }
}
