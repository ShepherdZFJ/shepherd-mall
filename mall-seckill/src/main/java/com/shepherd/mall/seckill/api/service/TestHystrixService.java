package com.shepherd.mall.seckill.api.service;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/9/1 10:32
 */
public interface TestHystrixService {

    String testHystrix(Long skuId);

    String testFeignWithHystrix(Long skuId);
}
