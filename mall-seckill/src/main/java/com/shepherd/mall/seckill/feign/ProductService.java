package com.shepherd.mall.seckill.feign;

import com.shepherd.mall.seckill.dto.SkuInfo;
import com.shepherd.mall.seckill.factory.ProductFeignFactory;
import com.shepherd.mall.seckill.service.ProductFeignFallback;
import com.shepherd.mall.vo.ResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/26 14:50
 */

/**
 * 在网络请求时，可能会出现异常请求，如果还想再异常情况下使系统可用，那么就需要容错处理，比如:网络请求超时时给用户提示“稍后重试”或使用本地快照数据等等。
 *
 * Spring Cloud Feign就是通过Fallback实现的，有两种方式：
 *
 * 1、@FeignClient.fallback = ProductFeignFallback.class指定一个实现Feign接口的实现类。
 *
 * 2、@FeignClient.fallbackFactory = UserFeignFactory.class指定一个实现FallbackFactory<T>工厂接口类
 *
 * 因为Fallback是通过Hystrix实现的， 所以需要开启Hystrix，spring boot application.properties文件配置feign.hystrix.enabled=true，这样就开启了Fallback

 */
@FeignClient(name = "${micro-server.mall-product}", path = "/api/mall/product", fallback = ProductFeignFallback.class)
//@FeignClient(name = "${micro-server.mall-product}", path = "/api/mall/product", fallbackFactory = ProductFeignFactory.class)
//@FeignClient(name = "${micro-server.mall-product}", path = "/api/mall/product")
public interface ProductService {

    @GetMapping("/sku/{skuId}")
    ResponseVO<SkuInfo> getSku(@PathVariable("skuId") Long skuId);



}

