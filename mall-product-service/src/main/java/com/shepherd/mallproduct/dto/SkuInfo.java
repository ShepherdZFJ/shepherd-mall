package com.shepherd.mallproduct.dto;

import com.shepherd.mallproduct.entity.ProductSku;
import com.shepherd.mallproduct.entity.ProductSpu;
import lombok.Data;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/6/24 18:08
 */
@Data
public class SkuInfo {
    private ProductSku sku;
    private ProductSpu spu;
    private SpecDTO spec;
    private BrandDTO brand;
    private CategoryDTO category;

}
