package com.shepherd.mallorder.api.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/23 00:09
 */
@Data
public class OrderSumbitVO {
    private Long addressId;
    private Integer payType;
    private String token;
    private BigDecimal payAmount;
    private String remark;
}

