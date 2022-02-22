package com.shepherd.mall.seckill.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.shepherd.mall.vo.ResponseVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/9/2 11:56
 */

@Configuration
public class SentinelConfig  {


    @Bean
    public BlockExceptionHandler blockExceptionHandler() {
        return (httpServletRequest, httpServletResponse, e) -> {
            ResponseVO<Object> responseVO = ResponseVO.failure(400, "太多的请求");
            httpServletResponse.setContentType("application/json;charset=utf-8");
            httpServletResponse.getWriter().write(JSON.toJSONString(responseVO));
        };
    }
}
