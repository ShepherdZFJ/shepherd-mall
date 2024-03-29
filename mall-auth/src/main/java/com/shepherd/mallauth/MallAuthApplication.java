package com.shepherd.mallauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@MapperScan("com.shepherd.mallauth.dao")
@ConfigurationPropertiesScan
public class MallAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallAuthApplication.class, args);
	}

}
