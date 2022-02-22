package com.shepherd.mall.seckill.factory;

import com.shepherd.mall.seckill.feign.ProductService;
import com.shepherd.mall.seckill.service.ProductFeignFallback;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/9/1 15:56
 */
@Component
@Slf4j
public class ProductFeignFactory implements FallbackFactory<ProductService> {
    @Resource
    private ProductFeignFallback productFeignFallback;

    @Override
    public ProductService create(Throwable throwable) {
        log.info("执行ProductFeignFactory了");
        throwable.printStackTrace();
        log.error("异常信息：{}", throwable.getMessage());
        return productFeignFallback;
    }
}
