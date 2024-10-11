package com.michibaum.usermanagement_library

import org.springframework.cloud.openfeign.FeignClient

@FeignClient(value = "usermanagement-service")
interface UsermanagementClient : UserManagementEndpoints