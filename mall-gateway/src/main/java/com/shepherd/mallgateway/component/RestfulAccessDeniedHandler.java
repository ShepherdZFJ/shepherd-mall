package com.shepherd.mallgateway.component;

import cn.hutool.json.JSONUtil;
import com.shepherd.mall.enums.ResponseStatusEnum;
import com.shepherd.mall.vo.ResponseVO;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;


/**
 * 自定义返回结果：没有权限访问时
 */
//@Component
public class RestfulAccessDeniedHandler {
//    @Override
//    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
//        ServerHttpResponse response = exchange.getResponse();
//        response.setStatusCode(HttpStatus.OK);
//        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//        response.getHeaders().set("Access-Control-Allow-Origin","*");
//        response.getHeaders().set("Cache-Control","no-cache");
//        String body= JSONUtil.toJsonStr(ResponseVO.failure(ResponseStatusEnum.FORBIDDEN, denied.getMessage()));
//        DataBuffer buffer =  response.bufferFactory().wrap(body.getBytes(Charset.forName("UTF-8")));
//        return response.writeWith(Mono.just(buffer));
//    }
}
