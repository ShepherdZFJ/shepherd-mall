package com.shepherd.mallproduct.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/2 18:54
 */
@Data
public class BrandVO {

    private Long brandId;
    @NotNull(message = "categoryId不能为空")
    private Long categoryId;
    @NotBlank(message = "name不能为空")
    private String name;
    private String image;
    private String description;
    private String letter;
    private List<Long> brandIds;
}
