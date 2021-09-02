package com.shepherd.mall.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 注意引入hystrix依赖就不能引入sentinel依赖
 * 引入sentinel详情：
 * 1、注意要使用高版本的fastjson，否则在控制台新增流控，熔断规则保存之后不显示。
 * 2.每个服务引入sentinel依赖之后，需要在配置文件添加如下配置：
 *spring:
 *   cloud:
 *     sentinel:
 *       transport:
 *         port: 8719
 *         dashboard: localhost:8080
 * 3.引入sentinel依赖后springboot所有资源默认都是可以监控的，所以不需要额外配置
 * 4.在sentinel控制台给服务的某些接口设置了流控规则，重启之后被清空了，这是因为sentinel默认数据保存在服务内存
 * 5.限流
 * 6.熔断降级：使用sentinel来保护feign远程调用
 *  1）调用方的熔断保护开启：和开启hystrix一样设置，feign.sentinel.enabled = true
 *  2）调用方手动在sentinel控制台设置指定远程服务的降级策略。远程服务被降级处理。触发我们的熔断方法回调方法
 *  3）高并发请求时，为了保证核心功能正常服务，必须牺牲一些不那么重要的远程服务。这个时候我们不需要在调用方设置熔断，直接针对服务提供者
 *  指定降级策略，这样保证调用方调用该服务时会触发对应的降级策略，返回默认降级数据兜底
 * 7.自定义保护资源
 *  1）基于抛出异常的方式定义资源
 *  2）基于注解自定义资源
 *  以上两种方式都需要配置号被限流后的默认返回，否则会直接抛出异常信息
 *  如果是接口类型的资源限流后，我们可以设置统一返回：注册一个BlockExceptionHandler类型的bean， 并重写方法即可
 *
 * 8.每个服务都需要引入下面依赖：
 *         <dependency>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-starter-actuator</artifactId>
 *         </dependency>
 * 然后再配置文件中配置management.endpoints.web.exposure.include=*，这时候在sentinel控制台对应的服务的实时监控才会有数据
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.shepherd.mall.seckill.dao")
@ComponentScan(basePackages = {"com.shepherd"})
@EnableFeignClients(basePackages = "com.shepherd.mall.seckill")
@EnableRabbit
@EnableScheduling
//@EnableHystrix
public class MallSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallSeckillApplication.class, args);
    }

}
