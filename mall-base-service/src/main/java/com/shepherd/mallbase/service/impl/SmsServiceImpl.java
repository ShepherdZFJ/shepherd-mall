package com.shepherd.mallbase.service.impl;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.shepherd.mall.exception.BusinessException;
import com.shepherd.mallbase.api.service.SmsService;
import com.shepherd.mallbase.enums.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/12/28 22:03
 */
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {


    private static final String VERIFICATION = "shepherd-mall-code-";


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${aliyun-sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun-sms.accessSecret}")
    private String accessSecret;

    @Value("${aliyun-sms.signName}")
    private String signName;

    @Value("${aliyun-sms.templateCode}")
    private String templateCode;
    @Value("${aliyun-sms.expireTime}")
    private Long expireTime;

    @Override
    public String getSmsCode(String phoneNumber) {
        String code = RandomStringUtils.randomNumeric(6);
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("TemplateParam", "{code:" + code + "}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info("send message result: " + response.getData());
            stringRedisTemplate.opsForValue().set(VERIFICATION + phoneNumber, code, expireTime, TimeUnit.MINUTES);
            return code;

        } catch (ServerException e) {
            log.error("send message error: ", e);
            throw new BusinessException(ErrorCodeEnum.SEND_MESSAGE_ERROR.getCode(), ErrorCodeEnum.SEND_MESSAGE_ERROR.getMessage());
        } catch (ClientException e) {
            log.error("send message error: ", e);
            throw new BusinessException(ErrorCodeEnum.SEND_MESSAGE_ERROR.getCode(), ErrorCodeEnum.SEND_MESSAGE_ERROR.getMessage());
        }
    }


}
