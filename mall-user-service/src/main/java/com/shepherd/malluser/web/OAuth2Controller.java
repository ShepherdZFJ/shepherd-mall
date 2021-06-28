package com.shepherd.malluser.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.shepherd.mall.utils.HttpUtils;
import com.shepherd.malluser.dto.SocialUser;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/6/28 16:27
 */
@Controller
@Api("第三方登录接口")
//@RequestMapping("/api/mall/oauth2")
@Slf4j
public class OAuth2Controller {

//    private final MemberFeignService memberFeignService;
//
//    @Autowired
//    public OAuth2Controller(MemberFeignService memberFeignService) {
//        this.memberFeignService = memberFeignService;
//    }

    @GetMapping(value = "/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {

        Map<String, String> map = new HashMap<>(5);
        map.put("client_id", "18343931");
        map.put("client_secret", "37a268a9a6efb9f7cc6dc0bc149a2624");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code", code);

        //1、根据用户授权返回的code换取access_token
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", new HashMap<>(), map, new HashMap<>());

        //2、处理
        if (response.getStatusLine().getStatusCode() == 200) {
            //获取到了access_token,转为通用社交登录对象
            String json = EntityUtils.toString(response.getEntity());
            //String json = JSON.toJSONString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

            //知道了哪个社交用户
            //1）、当前用户如果是第一次进网站，自动注册进来（为当前社交用户生成一个会员信息，以后这个社交账号就对应指定的会员）
            //登录或者注册这个社交用户
            System.out.println(socialUser.getAccess_token());
//            //调用远程服务
//            R oauthLogin = memberFeignService.oauthLogin(socialUser);
//            if (oauthLogin.getCode() == 0) {
//                MemberResponseVo data = oauthLogin.getData("data", new TypeReference<MemberResponseVo>() {
//                });
//                log.info("登录成功：用户信息：{}", data.toString());
//
//                //1、第一次使用session，命令浏览器保存卡号，JSESSIONID这个cookie
//                //以后浏览器访问哪个网站就会带上这个网站的cookie
//                //TODO 1、默认发的令牌。当前域（解决子域session共享问题）
//                //TODO 2、使用JSON的序列化方式来序列化对象到Redis中
//                session.setAttribute(LOGIN_USER, data);
//
//                //2、登录成功跳回首页
//                return "redirect:http://gulimall.com";
//            } else {
//                return "redirect:http://auth.gulimall.com/login.html";
//            }
            return "redirect:http://gulimall.com";
        } else {
            return "redirect:http://auth.gulimall.com/login.html";
        }

    }

}
