package com.shepherd.mallproduct.query;

import com.shepherd.mall.base.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2022/6/6 11:06
 */
@Data
public class BrandQuery extends BaseQuery {
    @ApiModelProperty("类目id")
    private Long categoryId;
    @ApiModelProperty("品牌名称")
    private String name;
    @ApiModelProperty("品牌首字母")
    private String letter;
}
