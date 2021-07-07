package com.shepherd.mallorder.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/5 16:39
 */
@Data
public class ProductSku {
    private Long skuId;
    private Long spuId;
    private String name;
    private BigDecimal price;
    private String mainImage;
    private String subImage;
    private Integer weight;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private Long saleCount;
    private String spec;
    private Map<String, Object> specMap;

    /**
     * 是否有库存
     */
    private Boolean hasStock;
    /**
     * 热度
     */
    private Long hotScore;
    private String brandImg;
}
