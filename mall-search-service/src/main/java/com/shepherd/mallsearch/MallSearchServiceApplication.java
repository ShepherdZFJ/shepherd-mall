package com.shepherd.mallsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


@EnableEurekaClient
@ComponentScan(basePackages = {"com.shepherd"})
@EnableFeignClients(basePackages = "com.shepherd.mallsearch")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MallSearchServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(MallSearchServiceApplication.class, args);
    }

}
