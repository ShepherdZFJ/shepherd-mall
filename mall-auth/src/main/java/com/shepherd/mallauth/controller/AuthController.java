package com.shepherd.mallauth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shepherd.mallauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController("/api/mall/auth")
@RequiredArgsConstructor
public class AuthController {


    private AuthService authService;


    @PostMapping("login")
    public String login(@RequestParam("phone") String phone,
                        @RequestParam("code") String code) {
        return authService.login(phone, code);
    }

    @GetMapping("/code/{phoneNumber}")
    public String code(@PathVariable("phoneNumber") String phoneNumber) {
        return authService.getCode(phoneNumber);
    }

    @PutMapping("logout")
    public void logout(){

    }


}