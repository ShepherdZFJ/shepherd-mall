package com.shepherd.mallorder.api.vo;

import com.shepherd.mallorder.dto.CartItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/1 15:53
 */
@Data
public class CartVO {
    private List<CartItem> items;

    private Integer countNum;

    private Integer countCheck;

    private BigDecimal totalAmount;

    private List<Long> skuIds;

}
