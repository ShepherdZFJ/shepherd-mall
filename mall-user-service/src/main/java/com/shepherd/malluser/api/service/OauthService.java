package com.shepherd.malluser.api.service;

import com.shepherd.malluser.api.vo.LoginResponseVO;
import com.shepherd.malluser.dto.UserDTO;

import javax.servlet.http.HttpSession;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/6/29 14:34
 */
public interface OauthService {

    LoginResponseVO weibo(String code);

    void bind(Long thirdOauthId, String phoneNumber, HttpSession session);

}
