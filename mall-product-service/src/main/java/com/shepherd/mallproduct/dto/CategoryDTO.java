package com.shepherd.mallproduct.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shepherd.mallproduct.entity.Category;
import lombok.Data;

import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 16:32
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class CategoryDTO extends Category {
    private Long categoryId;
    private List<CategoryDTO> nodeList;

}
