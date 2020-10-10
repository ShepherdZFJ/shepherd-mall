package com.shepherd.mallproduct.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mallproduct.api.service.CategoryService;
import com.shepherd.mallproduct.dto.CategoryDTO;
import com.shepherd.mallproduct.query.CategoryQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 17:27
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    @Override
    public IPage<CategoryDTO> getCategoryList(CategoryQuery query) {
        if (query.getPageNo() == null) {
            query.setPageNo(CommonConstant.DEFAULT_PAGE_NO);
        }
        if (query.getPageSize() == null) {
            query.setPageSize(CommonConstant.DEFAULT_PAGE_SIZE);
        }
        return null;
    }
}
