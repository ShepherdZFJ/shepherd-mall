package com.shepherd.mallproduct.dto;

import com.shepherd.mallproduct.entity.ProductSku;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/4/30 0:58
 */

@Data
public class ProductSkuDTO extends ProductSku {
    private Long skuId;
    private String brandName;
    private String categoryName;
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
