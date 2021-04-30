package com.shepherd.mallproduct;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = "com.shepherd.mallproduct.dao")
@ComponentScan(basePackages = {"com.shepherd"})
@EnableFeignClients(basePackages = "com.shepherd.mallproduct")
public class MallProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallProductServiceApplication.class, args);
    }

}
