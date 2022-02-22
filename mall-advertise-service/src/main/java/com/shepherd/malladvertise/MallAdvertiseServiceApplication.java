package com.shepherd.malladvertise;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication

//启用canal
@EnableCanalClient
@MapperScan(basePackages = "com.shepherd.malladvertise.dao")
@ComponentScan(basePackages = {"com.shepherd"})
public class MallAdvertiseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallAdvertiseServiceApplication.class, args);
    }

}
