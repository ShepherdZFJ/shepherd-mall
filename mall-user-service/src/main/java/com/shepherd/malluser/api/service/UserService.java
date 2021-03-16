package com.shepherd.malluser.api.service;

import com.shepherd.malluser.api.vo.UserVO;
import com.shepherd.malluser.dto.TokenResponse;
import com.shepherd.malluser.dto.UserDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/11/17 23:23
 */
public interface UserService {
    String getCode(String phoneNumber);

    TokenResponse login(UserVO userVO);

    void update(UserDTO userDTO);

    UserDTO status(HttpServletRequest request, HttpServletResponse response);

    List<UserDTO> getList();

    void logout(HttpServletRequest request, HttpServletResponse response);

    void retrievePassword(UserDTO userDTO);

    UserDTO loadUserByUsername(String name);

}
