package com.shepherd.mallauth.service;

import com.shepherd.mallauth.dto.SecurityUser;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserService {

    SecurityUser loadUser(@RequestParam String username);
}
