package com.shepherd.ware.dto;

import lombok.Data;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/26 15:13
 */
@Data
public class Order {
    private Long orderId;
    private String orderNo;
    private Integer status;
    List<OrderItem> orderItemList;
}
