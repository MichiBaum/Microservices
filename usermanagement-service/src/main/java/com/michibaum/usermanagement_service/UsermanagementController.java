package com.michibaum.usermanagement_service;

import com.michibaum.usermanagement_library.LoginDto;
import com.michibaum.usermanagement_library.UserManagementEndpoints;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsermanagementController implements UserManagementEndpoints {


    @Override
    public Boolean checkPassword(LoginDto loginDto) {
        return true;
    }
}
