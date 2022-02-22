package com.shepherd.mall.seckill.api.service;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/9/2 00:20
 */
public interface TestSentinelService {
    String degrade(Long skuId);
    String customResource();
    String annotationCustomResource() throws InterruptedException;
}
