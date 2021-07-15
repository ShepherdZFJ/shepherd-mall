package com.shepherd.mallauth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mallauth.dao.UserDAO;
import com.shepherd.mallauth.dto.SecurityUser;
import com.shepherd.mallauth.entity.User;
import com.shepherd.mallauth.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl extends ServiceImpl<UserDAO, User> implements UserService {

    @Resource
    private UserDAO userDAO;

//    @Override
//    public SecurityUser loadUser(String username) {
//        final LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        userLambdaQueryWrapper.eq(User::getNickname, username);
//        User user = userDAO.selectOne(userLambdaQueryWrapper);
//        SecurityUser securityUser = new SecurityUser();
//        MallBeanUtil.copy(user, securityUser);
//        securityUser.setUsername(user.getNickname());
//        securityUser.setClientId("mall-user");
//        return securityUser;
//    }
}
