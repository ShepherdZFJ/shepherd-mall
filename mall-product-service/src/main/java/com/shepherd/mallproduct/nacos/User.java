package com.shepherd.mallproduct.nacos;

import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2022/4/20 14:55
 */
@Data
@Configuration
@NacosConfigurationProperties(prefix = "user", autoRefreshed = true, dataId = "mall-product-service-dev.yaml")
public class User {
    private String name;
    private Integer age;
    private Long id;
}
