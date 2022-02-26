package com.shepherd.malluser.test;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2022/2/23 17:00
 */
@Configuration
public class MyConfiguration {
    @Bean
    public RunService trainRunServiceImpl() {
        return new TrainRunServiceImpl();
    }

//    @Order(2)
    @Bean
    @ConditionalOnMissingBean // 没传参数，代表如果没有方法返回类型RunService这个bean就注册当前bean
    public RunService carRunServiceImpl() {
        return new CarRunServiceImpl();
    }
    /**
     * 当注入trainRunServiceImpl代码在carRunServiceImpl之后， @ConditionalOnMissingBean失效，执行注入carRunServiceImpl时查找
     * 是否有RunService类型的bean，没找到，说明此时trainRunServiceImpl还没有注入，在初始化bean的时候，顺序出现了问题。
     * 注意在配置类，加入@Order注解并不能指定我们想要注册bean的顺序
     * {@link Order}使用误区：
     * 1.在加@Component注解的类指定了@order注解，认为值小的高优先级的类会先注册到容器，是错误的。
     * 2.在配置类的使用@Bean注解方法注册bean时使用@Order 认为值小的高优先级的类会先注册到容器，是错误的。
     */

//    @Order(1)
//    @Bean
//    public RunService trainRunServiceImpl() {
//        return new TrainRunServiceImpl();
//    }
}
