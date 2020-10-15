package com.shepherd.mallproduct.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/13 18:37
 */
@Configuration
public class InitConfig {


    @Bean
    public PaginationInterceptor paginationInterceptor() {
        //必须注入这个分页插件拦截器，否则分页不成功
        return new PaginationInterceptor();
    }
}
