package com.shepherd.mallproduct.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shepherd.mallproduct.dto.CategoryDTO;
import com.shepherd.mallproduct.dto.ProductDTO;
import com.shepherd.mallproduct.query.CategoryQuery;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 16:27
 */
public interface CategoryService {
    IPage<CategoryDTO> getCategoryList(CategoryQuery query);
}
