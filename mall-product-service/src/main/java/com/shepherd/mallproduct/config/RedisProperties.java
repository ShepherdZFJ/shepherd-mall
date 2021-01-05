package com.shepherd.mallproduct.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/1/5 16:59
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedisProperties {
    private String host = "localhost";

    private Integer port = 6379;

    private String password;

    private Integer database = 0;
}

