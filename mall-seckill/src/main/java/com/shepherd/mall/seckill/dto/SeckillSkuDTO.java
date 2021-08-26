package com.shepherd.mall.seckill.dto;

import com.shepherd.mall.seckill.entity.SeckillSku;
import lombok.Data;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/26 14:52
 */
@Data
public class SeckillSkuDTO extends SeckillSku {
    private SkuInfo skuInfo;

    //当前商品秒杀的开始时间
    private Long startTime;

    //当前商品秒杀的结束时间
    private Long endTime;

    //当前商品秒杀的随机码
    private String randomCode;
}
