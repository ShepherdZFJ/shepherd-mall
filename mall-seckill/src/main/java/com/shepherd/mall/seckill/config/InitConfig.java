package com.shepherd.mall.seckill.config;

import com.shepherd.mall.utils.IdWorker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/27 17:52
 */
@Configuration
public class InitConfig {

    @Bean
    public IdWorker idWorker() {
        return new IdWorker();
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}