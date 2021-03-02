package com.shepherd.malluser.api.controller;

import com.shepherd.mall.annotation.ResponseResultBody;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.malluser.api.service.UserService;
import com.shepherd.malluser.api.vo.LoginVO;
import com.shepherd.malluser.api.vo.UserVO;
import com.shepherd.malluser.dto.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/11/17 23:21
 */
@RestController
@RequestMapping("/api/mall/user")
@Api("用户相关接口")
public class UserController {
    @Resource
    private UserService userService;

    @ResponseResultBody
    @GetMapping("/VerificationCode/{phoneNumber}")
    @ApiOperation("获取手机验证码")
    public void getCode(@PathVariable("phoneNumber") String phoneNumber) {
        userService.getCode(phoneNumber);
    }

    @ResponseResultBody
    @GetMapping("/status")
    @ApiOperation("查看登录状态")
    public UserVO status(HttpServletRequest request, HttpServletResponse response) {
        UserDTO userDTO = userService.status(request, response);
        return MallBeanUtil.copy(userDTO, UserVO.class);

    }

    @ResponseResultBody
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public LoginVO login(@RequestBody UserVO userVO, HttpServletRequest request, HttpServletResponse response) {
        UserDTO userDTO = MallBeanUtil.copy(userVO, UserDTO.class);
        UserDTO userDTO1 = userService.login(userDTO, request, response);
        return MallBeanUtil.copy(userDTO1, LoginVO.class);
    }

    @ResponseResultBody
    @PutMapping
    @ApiOperation("修改用户信息")
    public void updateUserInfo(@RequestBody UserVO userVO) {
        UserDTO userDTO = MallBeanUtil.copy(userVO, UserDTO.class);
        userService.update(userDTO);
    }

    @ResponseResultBody
    @ApiOperation("退出登陆")
    @PutMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request, response);
    }

    @ResponseResultBody
    @GetMapping
    @ApiOperation("获取用户列表")
    public List<UserDTO> getList() {
        return userService.getList();
    }


    @ResponseResultBody
    @ApiOperation("找回密码")
    @PutMapping("/retrievePassword")
    public void retrievePassword(@RequestBody UserVO userVO) {
        UserDTO userDTO = MallBeanUtil.copy(userVO, UserDTO.class);
        userService.retrievePassword(userDTO);
    }

    @ApiOperation("根据用户名查询用户")
    @GetMapping("loadUser")
    public UserDTO loadUserByUsername(@RequestParam String username) {
        return userService.loadUserByUsername(username);
    }


}
