package com.shepherd.mallauth.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aliyun-sms")
@Data
public class SmsProperties {

    private String accessKeyId;

    private String accessSecret;

    private String signName;

    private String templateCode;

    private Long expireTime;
}