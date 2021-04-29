package com.shepherd.malluser.api.service;

import com.shepherd.malluser.dto.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("mall-auth-service")
public interface AuthService {
    @PostMapping("/oauth/token")
    TokenResponse getToken(@RequestParam Map<String, String> params);
}
