package com.michibaum.authentication_service.config

import org.springframework.cloud.openfeign.FeignClient
import com.michibaum.usermanagement_library.UserManagementEndpoints

@FeignClient(value = "usermanagement-service")
interface UsermanagementClient : UserManagementEndpoints