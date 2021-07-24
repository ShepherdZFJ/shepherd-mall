package com.shepherd.mallorder.config;

import com.shepherd.mall.utils.IdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/23 16:55
 */
@Configuration
public class InitConfig {

    @Bean
    public IdWorker idWorker() {
        return new IdWorker();
    }
}
