package com.shepherd.ware.dto;

import lombok.Data;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/26 15:14
 */
@Data
public class OrderItem {
    private Long skuId;
    private String skuName;
    private Integer number;
}
