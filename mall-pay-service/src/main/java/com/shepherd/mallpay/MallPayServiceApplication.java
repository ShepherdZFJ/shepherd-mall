package com.shepherd.mallpay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.shepherd.mallpay.dao")
public class MallPayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallPayServiceApplication.class, args);
    }

}
