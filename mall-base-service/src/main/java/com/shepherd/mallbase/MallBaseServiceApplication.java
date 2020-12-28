package com.shepherd.mallbase;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan(basePackages = {"com.shepherd"})
public class MallBaseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallBaseServiceApplication.class, args);
    }

}
