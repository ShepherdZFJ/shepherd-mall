package com.shepherd.malluser;

import com.shepherd.mall.base.CasProperties;
import com.shepherd.mall.utils.CookieBaseSessionUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = "com.shepherd.malluser.dao")
@ComponentScan(basePackages = {"com.shepherd"})
@EnableFeignClients
public class MallUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallUserServiceApplication.class, args);
    }

    @Bean
    public CasProperties casProperties() {
        return new CasProperties();
    }

    @Bean
    public CookieBaseSessionUtil cookieBasedSession(CasProperties casProperties) {
        CookieBaseSessionUtil cookieBasedSession = new CookieBaseSessionUtil();
        cookieBasedSession.setCasProperties(casProperties);
        return cookieBasedSession;
    }

}
