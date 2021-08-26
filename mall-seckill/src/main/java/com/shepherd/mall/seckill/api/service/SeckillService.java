package com.shepherd.mall.seckill.api.service;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/25 23:50
 */
public interface SeckillService {
    /**
     * 上架最近3天秒杀活动信息和关联商品到redis中，预热
     */
    void upSeckillSessionLast3Day();
}
