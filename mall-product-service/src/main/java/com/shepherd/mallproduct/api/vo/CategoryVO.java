package com.shepherd.mallproduct.api.vo;

import com.shepherd.mallproduct.dto.CategoryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 16:38
 */
@Data
public class CategoryVO {

    private Long categoryId;
    private Long parentId;
    private String name;
    private Boolean status;
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;
    private Integer isDelete;
    private List<CategoryDTO> categoryDTOList;
}
