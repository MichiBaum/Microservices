package com.michibaum.authentication_service.config;

import com.michibaum.usermanagement_library.UserManagementEndpoints;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "usermanagement-service")
public interface UsermanagementClient extends UserManagementEndpoints {
}
