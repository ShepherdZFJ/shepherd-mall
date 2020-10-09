package com.shepherd.mallpay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/10/8 22:42
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx")
public class WxProperties {
    private String appId;

    private String mchId;

    private String mchKey;

    private String notifyUrl;

    private String returnUrl;
}
