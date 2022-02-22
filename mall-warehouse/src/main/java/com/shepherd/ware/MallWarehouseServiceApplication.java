package com.shepherd.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.shepherd.ware.dao")
@ComponentScan(basePackages = {"com.shepherd"})
@EnableFeignClients(basePackages = "com.shepherd.ware")
@EnableRabbit
public class MallWarehouseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallWarehouseServiceApplication.class, args);
    }

}
