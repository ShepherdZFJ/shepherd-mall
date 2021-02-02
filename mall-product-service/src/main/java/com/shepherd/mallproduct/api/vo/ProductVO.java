package com.shepherd.mallproduct.api.vo;

import com.shepherd.mallproduct.entity.ProductSku;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 16:37
 */
@Data
public class ProductVO {

    private Long categoryId;
    private Long brandId;
    private String name;
    private String subtitle;
    private String mainImage;
    private String subImages;
    private String detail;
    private String specItems;
    private String paramItems;
    private Long productSpuId;
    private List<Long> productSpuIds;
    private List<ProductSku> skuList;
}
