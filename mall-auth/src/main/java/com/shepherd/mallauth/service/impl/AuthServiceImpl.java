package com.shepherd.mallauth.service.impl;


import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shepherd.mallauth.config.JwtTokenConfig;
import com.shepherd.mallauth.config.SmsProperties;
import com.shepherd.mallauth.constant.RedisConstant;
import com.shepherd.mallauth.entity.User;
import com.shepherd.mallauth.service.AuthService;
import com.shepherd.mall.exception.BusinessException;
import com.shepherd.mallbase.enums.ErrorCodeEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.jwt.Jwt;

import java.security.KeyPair;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private UserServiceImpl userService;

    private RedisTemplate<String, String> redisTemplate;

    private SmsProperties properties;

    /**
     * 根据手机号来获取验证码
     * @param phoneNumber
     * @return
     */
    @Override
    public String getCode(String phoneNumber) {
        String code = RandomStringUtils.randomNumeric(6);
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", properties.getAccessKeyId(), properties.getAccessSecret());
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("SignName", properties.getSignName());
        request.putQueryParameter("TemplateCode", properties.getTemplateCode());
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("TemplateParam", "{code:" + code + "}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info("send message result: " + response.getData());
            redisTemplate.opsForValue().set(String.format(RedisConstant.VERIFICATION, phoneNumber), code, properties.getExpireTime(), TimeUnit.MINUTES);
            return code;

        } catch (ServerException e) {
            log.error("send message error: ", e);
            throw new BusinessException(ErrorCodeEnum.SEND_MESSAGE_ERROR.getCode(), ErrorCodeEnum.SEND_MESSAGE_ERROR.getMessage());
        } catch (ClientException e) {
            log.error("send message error: ", e);
            throw new BusinessException(ErrorCodeEnum.SEND_MESSAGE_ERROR.getCode(), ErrorCodeEnum.SEND_MESSAGE_ERROR.getMessage());
        }
    }

    @Override
    public String login(String phone, String code) {
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));

        return null;
    }

    public static void main(String[] args) {
        JwtTokenConfig config = new JwtTokenConfig();
        KeyPair keyPair = config.keyPair();
        User user = new User();
        user.setPhone("110");
        user.setId(11L);

        String token = Jwts.builder()
                //SECRET_KEY是加密算法对应的密钥，这里使用额是HS256加密算法
                .signWith(SignatureAlgorithm.HS256, "shepherd-mall")
                //expTime是过期时间
                .setExpiration(new Date())
                .claim("user", user)
                //该方法是在JWT中加入值为vaule的key字段
                .compact();

        System.out.println("token => " + token);

    }


}

