package com.shepherd.mall.seckill.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/25 23:25
 * id
 * promotion_id
 * session_id
 * sku_id
 * seckill_price
 * total_number
 * limit_number
 * sort
 * is_delete
 */
@Data
public class SeckillSku {
    private Long id;
    private Long promotionId;
    private Long sessionId;
    private Long skuId;
    private BigDecimal seckillPrice;
    private Integer totalNumber;
    private Integer limitNumber;
    private Integer sort;
    private Integer isDelete;

}
