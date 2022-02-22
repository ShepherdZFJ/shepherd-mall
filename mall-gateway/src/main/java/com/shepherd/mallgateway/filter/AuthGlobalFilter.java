package com.shepherd.mallgateway.filter;


import com.shepherd.mall.utils.JwtUtil;
import com.shepherd.mallgateway.constant.AuthConstant;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.text.ParseException;

/**
 * 将登录用户的JWT转化成用户信息的全局过滤器
 * 按照相关规范。微服务请求的携带信息一般都放在header里面
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {


    private static Logger LOGGER = LoggerFactory.getLogger(AuthGlobalFilter.class);
    private static final String AUTHORIZE_TOKEN = "authorization";

//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String token = exchange.getRequest().getHeaders().getFirst(AuthConstant.JWT_TOKEN_HEADER);
//        if (StrUtil.isEmpty(token)) {
//            return chain.filter(exchange);
//        }
//        try {
//            //从token中解析用户信息并设置到Header中去
//            String realToken = token.replace(AuthConstant.JWT_TOKEN_PREFIX, "");
//            JWSObject jwsObject = JWSObject.parse(realToken);
//            String userStr = jwsObject.getPayload().toString();
//            LOGGER.info("AuthGlobalFilter.filter() user:{}",userStr);
//            ServerHttpRequest request = exchange.getRequest().mutate().header(AuthConstant.USER_TOKEN_HEADER, userStr).build();
//            exchange = exchange.mutate().request(request).build();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return chain.filter(exchange);
//    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取Request、Response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //获取请求的URI
        String path = request.getURI().getPath();

        //如果是登录、放行
        if (path.startsWith("/api/mall/user/login") || path.startsWith("/api/mall/product")) {
            //放行
            Mono<Void> filter = chain.filter(exchange);
            return filter;
        }
        //获取头文件中的令牌信息
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        //如果头文件中没有，则从请求参数中获取
        if (StringUtils.isBlank(token)) {
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        }
        //如果在请求头、请求参数中都没有，则在cookie中获取
        if (StringUtils.isBlank(token)) {
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            token = cookie==null ? null: cookie.getValue();
        }

        //如果为空，则输出错误代码
        if (StringUtils.isEmpty(token)) {
            //设置方法不允许被访问，405错误代码
            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            return response.setComplete();
        }

        //解析令牌数据
        try {
            Claims claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            //解析失败，响应401错误
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //放行
        //按照相关规范。微服务之间请求的携带信息一般都放在header里面
        request.mutate().header(AUTHORIZE_TOKEN, token);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
