package com.shepherd.mallorder.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/5 15:13
 */
@Data
public class CartDTO {
    private List<CartItem> items;

    private Integer countNum;

    private Integer countCheck;

    private BigDecimal totalAmount;
}
