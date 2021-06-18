package com.shepherd.mallproduct.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/6/18 14:47
 */
@Configuration
public class RedissonConfig {

    private static final String REDISSON_PREFIX = "redis://";
    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private String redisPort;



    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        // 1、创建配置
        Config config = new Config();
        // Redis url should start with redis:// or rediss://
        config.useSingleServer().setAddress(REDISSON_PREFIX+redisHost+":"+redisPort);

        // 2、根据 Config 创建出 RedissonClient 实例
        return Redisson.create(config);
    }
}
