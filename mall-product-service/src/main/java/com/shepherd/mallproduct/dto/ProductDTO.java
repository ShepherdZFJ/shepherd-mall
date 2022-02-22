package com.shepherd.mallproduct.dto;

import com.shepherd.mallproduct.entity.ProductSku;
import com.shepherd.mallproduct.entity.ProductSpu;
import lombok.Data;

import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 16:32
 */
@Data
public class ProductDTO extends ProductSpu {
    private Long productSpuId;
    private List<Long> productSpuIds;
    private List<ProductSku> skuList;
}
