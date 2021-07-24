package com.shepherd.mallorder.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/22 00:21
 */
@Data
public class OrderConfirmDTO {
    private List<Address> addressList;
    private List<CartItem> orderItemList;
    private Integer integration;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String token;

}
