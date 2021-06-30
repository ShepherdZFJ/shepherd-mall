package com.shepherd.malluser.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.shepherd.mall.exception.BusinessException;
import com.shepherd.mall.utils.HttpUtils;
import com.shepherd.malluser.api.service.OauthService;
import com.shepherd.malluser.api.service.UserService;
import com.shepherd.malluser.api.vo.LoginResponseVO;
import com.shepherd.malluser.constant.Constant;
import com.shepherd.malluser.dao.ThirdOauthDAO;
import com.shepherd.malluser.dao.UserDAO;
import com.shepherd.malluser.dto.WeiboUser;
import com.shepherd.malluser.dto.UserDTO;
import com.shepherd.malluser.entity.ThirdOauth;
import com.shepherd.malluser.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/6/29 14:37
 */
@Slf4j
@Service
public class OauthServiceImpl implements OauthService {
    @Resource
    private ThirdOauthDAO thirdOauthDAO;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private UserDAO userDAO;





    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponseVO weibo(String code) {
        Map<String, String> map = new HashMap<>(5);
        map.put("client_id", "18343931");
        map.put("client_secret", "37a268a9a6efb9f7cc6dc0bc149a2624");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://shepherd.com/success");
        map.put("code", code);
        //注意：这里的code是微博授权之后给的，根据code去获取微博的access_token，一旦获取之后code就会失效
        //1、根据用户授权返回的code换取access_token
        HttpResponse response = null;
        try {
            response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", new HashMap<>(), map, new HashMap<>());
        } catch (Exception e) {
            log.error("根据code调用微博获取access_token接口失败", e);
            throw new BusinessException("根据code调用微博获取access_token接口失败");
        }

        //2、处理
        if (response.getStatusLine().getStatusCode() == 200) {

            //获取到了access_token,转为通用社交登录对象
            String json = null;
            try {
                json = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //String json = JSON.toJSONString(response.getEntity());
            WeiboUser weiboUser = JSON.parseObject(json, WeiboUser.class);
            log.info(weiboUser.getAccess_token());
            Long uid = Long.valueOf(weiboUser.getUid());
            ThirdOauth thirdOauth = getThirdOauth(uid, Constant.THIRD_TYPE_WEIBO);
            if (thirdOauth == null) {
                //初次第三方登录，那么需要进行数据绑定功能
                ThirdOauth oauth = new ThirdOauth();
                oauth.setOutId(uid);
                oauth.setType(Constant.THIRD_TYPE_WEIBO);
                oauth.setAccessToken(weiboUser.getAccess_token());
                oauth.setExpireTime(new Date(weiboUser.getExpires_in() * 1000));
                oauth.setCreateTime(new Date());
                oauth.setUpdateTime(new Date());
                thirdOauthDAO.insert(oauth);
                String token = UUID.randomUUID().toString();
                LoginResponseVO loginResponseVO = new LoginResponseVO();
                loginResponseVO.setIsFirstLogin(Constant.IS_FIRST_LOGIN);
                loginResponseVO.setAccessToken(thirdOauth.getAccessToken());
                loginResponseVO.setThirdOauthId(oauth.getId());
                return loginResponseVO;
            } else {
                //已经登录过了，此时按照交互，是绑定过手机号的了，可以根据手机号查询user信息
                LambdaUpdateWrapper<ThirdOauth> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(ThirdOauth::getId, thirdOauth.getId());
                updateWrapper.set(ThirdOauth::getAccessToken, weiboUser.getAccess_token());
                updateWrapper.set(ThirdOauth::getExpireTime, new Date(weiboUser.getExpires_in() * 1000));
                updateWrapper.set(ThirdOauth::getUpdateTime, new Date());
                thirdOauthDAO.update(new ThirdOauth(), updateWrapper);
                User user = userDAO.selectById(thirdOauth.getUserId());
                String token = UUID.randomUUID().toString();
                stringRedisTemplate.opsForValue().set(token, JSON.toJSONString(user), 2, TimeUnit.HOURS);
                LoginResponseVO loginResponseVO = new LoginResponseVO();
                loginResponseVO.setIsFirstLogin(Constant.IS_NOT_FIRST_LOGIN);
                loginResponseVO.setToken(token);
                return loginResponseVO;

            }
        } else {
            throw new BusinessException("获取微博access_token返回结果状态不是200，请检查调用参数");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bind(Long thirdOauthId, String phoneNumber, HttpSession session) {
        UserDTO userDTO = userService.getUserInfoByPhoneNumber(phoneNumber);
        if (userDTO == null) {
            //说明此手机号之前没有注册过，此时往user表插入一条记录
            ThirdOauth oauth = thirdOauthDAO.selectById(thirdOauthId);
            Map<String, String> query = new HashMap<>();
            query.put("access_token", oauth.getAccessToken());
            query.put("uid", oauth.getOutId().toString());
            HttpResponse response = null;
            try {
                response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), query);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response.getStatusLine().getStatusCode() == 200) {
                //查询成功
                String json = null;
                try {
                    json = EntityUtils.toString(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JSONObject jsonObject = JSON.parseObject(json);
                String name = jsonObject.getString("name");
                String gender = jsonObject.getString("gender");
                String profileImageUrl = jsonObject.getString("profile_image_url");

                User user = new User();
                user.setPhone(phoneNumber);
                user.setUserNo("shepherd_"+String.valueOf(System.currentTimeMillis()));
                user.setLastLoginTime(new Date());
                user.setNickname(name);
                user.setGender("m".equals(gender) ? 1 : 0);
                user.setHeadPhoto(profileImageUrl);
                user.setCreateTime(new Date());
                //把用户信息插入到数据库中
                userDAO.insert(user);

                ThirdOauth thirdOauth = new ThirdOauth();
                thirdOauth.setId(thirdOauthId);
                thirdOauth.setUserId(user.getId());
                thirdOauth.setUpdateTime(new Date());
                thirdOauthDAO.updateById(thirdOauth);

                String token = UUID.randomUUID().toString();
                stringRedisTemplate.opsForValue().set(token, JSON.toJSONString(user), 2, TimeUnit.HOURS);
                session.setAttribute("loginSession", user);
            }


        }
    }

    ThirdOauth getThirdOauth(Long outId, Integer type) {
        LambdaQueryWrapper<ThirdOauth> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ThirdOauth::getOutId, outId);
        queryWrapper.eq(ThirdOauth::getType, type);
        ThirdOauth thirdOauth = thirdOauthDAO.selectOne(queryWrapper);
        return thirdOauth;
    }
}
