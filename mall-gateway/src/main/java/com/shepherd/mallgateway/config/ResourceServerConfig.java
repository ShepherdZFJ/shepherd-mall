package com.shepherd.mallgateway.config;

import cn.hutool.core.util.ArrayUtil;
import com.shepherd.mallgateway.authorization.AuthorizationManager;
import com.shepherd.mallgateway.component.RestAuthenticationEntryPoint;
import com.shepherd.mallgateway.component.RestfulAccessDeniedHandler;
import com.shepherd.mallgateway.constant.AuthConstant;
import com.shepherd.mallgateway.filter.IgnoreUrlsRemoveJwtFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import reactor.core.publisher.Mono;

/**
 * 资源服务器配置
 */
//todo 网关服务先不引用springSecurity框架进行鉴权，后期在再升级改造



@AllArgsConstructor
//@Configuration
//@EnableWebFluxSecurity
public class ResourceServerConfig {
//    private final AuthorizationManager authorizationManager;
//    private final IgnoreUrlsConfig ignoreUrlsConfig;
//    private final RestfulAccessDeniedHandler restfulAccessDeniedHandler;
//    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
//    private final IgnoreUrlsRemoveJwtFilter ignoreUrlsRemoveJwtFilter;

//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        http.oauth2ResourceServer().jwt()
//                .jwtAuthenticationConverter(jwtAuthenticationConverter());
//        //自定义处理JWT请求头过期或签名错误的结果
//        http.oauth2ResourceServer().authenticationEntryPoint(restAuthenticationEntryPoint);
//        //对白名单路径，直接移除JWT请求头
//        http.addFilterBefore(ignoreUrlsRemoveJwtFilter,SecurityWebFiltersOrder.AUTHENTICATION);
//        http.authorizeExchange()
//                .pathMatchers(ArrayUtil.toArray(ignoreUrlsConfig.getUrls(),String.class)).permitAll()//白名单配置
//                .anyExchange().access(authorizationManager)//鉴权管理器配置
//                .and().exceptionHandling()
//                .accessDeniedHandler(restfulAccessDeniedHandler)//处理未授权
//                .authenticationEntryPoint(restAuthenticationEntryPoint)//处理未认证
//                .and().csrf().disable();
//        return http.build();
//    }
//
//    @Bean
//    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
//        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(AuthConstant.AUTHORITY_PREFIX);
//        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(AuthConstant.AUTHORITY_CLAIM_NAME);
//        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
//        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
//    }

}
