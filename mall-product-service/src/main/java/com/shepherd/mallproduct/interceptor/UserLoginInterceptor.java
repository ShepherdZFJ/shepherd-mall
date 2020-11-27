package com.shepherd.mallproduct.interceptor;

import com.shepherd.mall.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/11/11 10:21
 */
@Slf4j
public class UserLoginInterceptor implements HandlerInterceptor {

    /**
     * true 表示继续流程，false表示中断
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)  {
        log.info("preHandle...");
        if (1==1) {
            throw new BusinessException("dfgdfgdf");
        }
        return false;
    }
}