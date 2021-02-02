package com.shepherd.mallproduct.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/2 18:54
 */
@Data
public class BrandVO {

    private Long brandId;
    private Long categoryId;
    private String name;
    private String image;
    private String description;
    private String letter;
    private List<Long> brandIds;
}
