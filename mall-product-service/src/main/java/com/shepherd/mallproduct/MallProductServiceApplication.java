package com.shepherd.mallproduct;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = "com.shepherd.mallproduct.dao")
@ComponentScan(basePackages = {"com.shepherd"})
public class MallProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallProductServiceApplication.class, args);
    }

}
