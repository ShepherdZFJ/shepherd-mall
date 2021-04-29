package com.shepherd.mallauth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.shepherd.mallauth.dto.SecurityUser;
import com.shepherd.mallauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailImpl implements UserDetailsService {

    @Autowired
    private UserService userService;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        SecurityUser securityUser = userService.loadUser(s);
        return securityUser;
    }
}
