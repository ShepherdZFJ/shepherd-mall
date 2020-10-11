package com.shepherd.mallproduct.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mallproduct.api.service.CategoryService;
import com.shepherd.mallproduct.dao.CategoryDAO;
import com.shepherd.mallproduct.dto.CategoryDTO;
import com.shepherd.mallproduct.entity.Category;
import com.shepherd.mallproduct.query.CategoryQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 17:27
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryDAO categoryDAO;




    @Override
    public List<CategoryDTO> getCategoryList() {
//        if (query.getPageNo() == null) {
//            query.setPageNo(CommonConstant.DEFAULT_PAGE_NO);
//        }
//        if (query.getPageSize() == null) {
//            query.setPageSize(CommonConstant.DEFAULT_PAGE_SIZE);
//        }
        QueryWrapper<Category>queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", CommonConstant.NOT_DEL);
        //queryWrapper.eq("status",)
        List<Category> categoryList = categoryDAO.selectList(queryWrapper);
        List<CategoryDTO> categoryDTOList = categoryList.stream().map(category -> toCategoryDTO(category)).collect(Collectors.toList());
        List<CategoryDTO> list = new ArrayList<>();
        listToTree(categoryDTOList, list);
        return list;
    }

    private List<CategoryDTO> listToTree(List<CategoryDTO> categoryDTOList, List<CategoryDTO> list) {
        for (CategoryDTO categoryDTO :categoryDTOList) {
            if (categoryDTO.getParentId() == null) {
                list.add(categoryDTO);
            }
            for (CategoryDTO node: categoryDTOList) {
                if (Objects.equals(node.getParentId(), categoryDTO.getCategoryId())) {
                    if (categoryDTO.getNodeList() == null) {
                        categoryDTO.setNodeList(new ArrayList<>());
                    }
                    categoryDTO.getNodeList().add(node);
                }
            }
        }
        return list;
    }



    @Override
    public Long addCategory(CategoryDTO categoryDTO) {
        Category category = MallBeanUtil.copy(categoryDTO, Category.class);
        category.setIsDelete(CommonConstant.NOT_DEL);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        category.setStatus(1);
        int insert = categoryDAO.insert(category);
        return category.getId();

    }

    @Override
    public Boolean delBatch(List<Long> categoryIds) {
        return null;
    }

    @Override
    public Boolean updateCategory(CategoryDTO categoryDTO) {
        return null;
    }

    @Override
    public Boolean updateCategory(List<CategoryDTO> list) {
        return null;
    }

    private CategoryDTO toCategoryDTO(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDTO categoryDTO = MallBeanUtil.copy(category, CategoryDTO.class);
        categoryDTO.setCategoryId(category.getId());
        return categoryDTO;
    }
}
