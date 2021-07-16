package com.shepherd.mallorder.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shepherd.mall.utils.JwtUtil;
import com.shepherd.mall.vo.LoginVO;
import io.jsonwebtoken.Claims;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/16 15:28
 */
@Component
@Data
public class AuthInterceptor implements HandlerInterceptor {
    private static final String AUTHORIZE_TOKEN = "authorization";
    public  static ThreadLocal<LoginVO> localUser = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //网关服务会把token放在header里面
        String token = request.getHeader(AUTHORIZE_TOKEN);
        //如果为空，则输出错误代码
        if (StringUtils.isBlank(token)) {
            //设置方法不允许被访问，405错误代码
            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
            return false;
        }

        //解析令牌数据
        try {
            Claims claims = JwtUtil.parseJWT(token);
            LoginVO loginVO = JSONObject.parseObject(claims.getSubject(), LoginVO.class);
            localUser.set(loginVO);
        } catch (Exception e) {
            e.printStackTrace();
            //解析失败，响应401错误
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
