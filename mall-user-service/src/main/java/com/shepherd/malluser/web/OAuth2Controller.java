package com.shepherd.malluser.web;

import com.alibaba.fastjson.JSON;
import com.shepherd.mall.utils.HttpUtils;
import com.shepherd.malluser.api.service.OauthService;
import com.shepherd.malluser.api.vo.LoginResponseVO;
import com.shepherd.malluser.dto.WeiboUser;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/6/28 16:27
 */
@RestController
@Api("第三方登录接口")
@RequestMapping("/api/mall/oauth2")
@Slf4j
public class OAuth2Controller {

    @Resource
    private OauthService oauthService;

    @GetMapping(value = "/weibo/success")
    public LoginResponseVO weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        LoginResponseVO loginResponseVO = oauthService.weibo(code);
        return loginResponseVO;
    }



    @GetMapping(value = "/weibo/bind/{thirdOauthId}")
    public void bind(@PathVariable("thirdOauthId") Long thirdOauthId, @RequestParam("phoneNumber") String phoneNumber, HttpSession session) {
        oauthService.bind(thirdOauthId, phoneNumber, session);
    }

}
