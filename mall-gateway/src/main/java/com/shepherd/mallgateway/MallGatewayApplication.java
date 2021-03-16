package com.shepherd.mallgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class MallGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallGatewayApplication.class, args);
	}

}
