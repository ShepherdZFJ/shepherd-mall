package com.shepherd.mallauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

import com.shepherd.mallauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController("/api/mall/auth")
@RequiredArgsConstructor
public class AuthController {


    private AuthService authService;


    @PostMapping("login")
    public String login() {
        return null;
    }

    @GetMapping("/code/{phoneNumber}")
    public String code(@PathVariable("phoneNumber") String phoneNumber) {
        authService.getCode(phoneNumber);
        return null;
    }

    @PutMapping("logout")
    public void logout(){

    }


}