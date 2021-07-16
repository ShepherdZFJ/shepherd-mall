package com.shepherd.mallorder.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/1 16:24
 */
@Data
public class CartItem implements Serializable {
    private Long skuId;

    private Integer isCheck ;

    private String name;

    private String image;

    private List<String> specValues;

    private BigDecimal price;

    private Integer number;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;
}
