package com.shepherd.mallauth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shepherd.mallauth.dto.SecurityUser;
import com.shepherd.mallauth.entity.User;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserService extends IService<User> {

//    SecurityUser loadUser(@RequestParam String username);
}
