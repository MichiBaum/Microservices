package com.michibaum.usermanagement_library;

import org.springframework.web.bind.annotation.PostMapping;

public interface UserManagementEndpoints {

    @PostMapping(value = "/checkPasword")
    Boolean checkPassword(LoginDto loginDto);

}
