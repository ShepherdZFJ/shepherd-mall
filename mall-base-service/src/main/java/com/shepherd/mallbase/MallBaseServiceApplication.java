package com.shepherd.mallbase;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.shepherd"})
public class MallBaseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallBaseServiceApplication.class, args);
    }

}
