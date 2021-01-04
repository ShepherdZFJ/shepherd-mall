package com.shepherd.mallproduct.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shepherd.mallproduct.dto.CategoryDTO;
import com.shepherd.mallproduct.dto.ProductDTO;
import com.shepherd.mallproduct.query.CategoryQuery;

import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 16:27
 */
public interface CategoryService {
    /**
     * 获取类目列表
     * @return
     */
    List<CategoryDTO> getCategoryList();

    /**
     * 新增类目
     * @param categoryDTO
     * @return
     */
    Long addCategory(CategoryDTO categoryDTO);

    /**
     * 批量删除类目
     * @param categoryIds
     * @return
     */
    Boolean delBatch(List<Long>categoryIds);

    /**
     * 更新类目
     * @param categoryDTO
     * @return
     */
    Boolean updateCategory(CategoryDTO categoryDTO);

    /**
     * 批量更新类目
     * @param list
     * @return
     */
    Boolean updateCategory(List<CategoryDTO>list);

    /**
     * 根据categoryId查询当前类目信息
     * @param categoryId
     * @return
     */
    CategoryDTO getCategory(Long categoryId);

    /**
     * 根据categoryId获取下级id集合
     * @param categoryId
     * @return
     */
    List<Long> getCategoryIds(Long categoryId);

    List<CategoryDTO> test();

}
