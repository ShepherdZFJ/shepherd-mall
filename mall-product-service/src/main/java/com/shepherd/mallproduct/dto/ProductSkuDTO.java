package com.shepherd.mallproduct.dto;

import com.shepherd.mallproduct.entity.ProductSku;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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

}
