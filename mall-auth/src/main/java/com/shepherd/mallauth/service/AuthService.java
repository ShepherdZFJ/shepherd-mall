package com.shepherd.mallauth.service;

import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    String getCode(String phoneNumber);

    String login(String phone, String code);;

}
