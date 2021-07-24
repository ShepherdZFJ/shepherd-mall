package com.shepherd.mallorder.api.vo;

import lombok.Data;

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
    private String payAmount;
    private String remark;
}

