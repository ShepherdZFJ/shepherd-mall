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

@RestController("/api/mall/auth")
public class AuthController {
    @Autowired
    private TokenEndpoint tokenEndpoint;
//    @PostMapping("/oauth/token")
//    public OAuth2AccessToken postToken(Principal principal, @RequestBody Map<String, String> params) throws HttpRequestMethodNotSupportedException {
//        return tokenEndpoint.postAccessToken(principal, params).getBody();
//    }
}
