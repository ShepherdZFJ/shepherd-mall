package com.shepherd.mallgateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.shepherd.mall.vo.ResponseVO;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/9/3 12:46
 */
@Configuration
public class SentinelConfig {

    public SentinelConfig() {
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                ResponseVO responseVO = new ResponseVO();
                responseVO.setCode(400);
                responseVO.setData("网关sentinel统一返回了");
                 Mono<ServerResponse> body = ServerResponse.ok().body(Mono.just(responseVO), ResponseVO.class);
                return body;
            }
        });
    }
}
