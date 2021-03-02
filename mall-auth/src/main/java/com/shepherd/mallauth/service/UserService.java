package com.shepherd.mallauth.service;

import com.shepherd.mallauth.dto.SecurityUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("mall-user-service")
public interface UserService {

    @GetMapping(value = "/api/mall/user/loadUser")
    SecurityUser loadUser(@RequestParam String username);
}
