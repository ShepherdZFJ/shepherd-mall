package com.shepherd.mall.seckill.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/27 17:27
 */
@Data
public class SeckillOrder {
    private Long userId;
    private Integer number;
    private Long skuId;
    private BigDecimal seckillPrice;
    private Long sessionId;
    private String orderNo;
}
