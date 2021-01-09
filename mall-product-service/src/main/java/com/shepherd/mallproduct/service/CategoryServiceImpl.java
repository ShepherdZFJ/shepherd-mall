package com.shepherd.mallproduct.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mallproduct.api.service.CategoryService;
import com.shepherd.mallproduct.cache.CompositeCache;
import com.shepherd.mallproduct.dao.CategoryDAO;
import com.shepherd.mallproduct.dto.CategoryDTO;
import com.shepherd.mallproduct.entity.Category;
import com.shepherd.mallproduct.query.CategoryQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
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
    @Resource
    private CompositeCache compositeCache;


    @Override
    public String test() {
        String str = compositeCache.get("category_cache", () -> method());
        if (StringUtils.isBlank(str)) {
            str = method();
        }
        return str;
    }

    public String method() {
        return "hello category ,mall";
    }



    @Override
    public List<CategoryDTO> getCategoryList() {
        List<CategoryDTO> categoryDTOList = compositeCache.get("category_cache", () -> getCategoryTree());
        if (CollectionUtils.isEmpty(categoryDTOList)) {
            categoryDTOList = getCategoryTree();
        }
        return categoryDTOList;
    }

    List<CategoryDTO> getCategoryTree() {
        List<CategoryDTO> categoryDTOList = getList();
        List<CategoryDTO> list = new ArrayList<>();
        listToTree(categoryDTOList, list);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
    public Boolean delBatch(List<Long> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return false;
        }
        UpdateWrapper<Category> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", categoryIds);
        updateWrapper.set("is_delete", CommonConstant.DEL);
        updateWrapper.set("update_time", new Date());
        int update = categoryDAO.update(new Category(), updateWrapper);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCategory(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            return false;
        }
        update(categoryDTO);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCategory(List<CategoryDTO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        list.forEach(categoryDTO -> {
            update(categoryDTO);
        });
        return true;
    }

    @Override
    public CategoryDTO getCategory(Long categoryId) {
        return null;
    }

    @Override
    public List<Long> getCategoryIds(Long categoryId) {
        List<CategoryDTO> categoryDTOList = getList();
        List<Long> ids = new ArrayList<>();
        ids.add(categoryId);
        getIds(categoryDTOList, categoryId, ids);
        return ids;
    }


    private List<Long> getIds(List<CategoryDTO>categoryDTOList, Long categoryId, List<Long> ids) {
        if (CollectionUtils.isEmpty(categoryDTOList)) {
            return null;
        }
        for (CategoryDTO categoryDTO: categoryDTOList) {
            if (Objects.equals(categoryDTO.getParentId(), categoryId)) {
                ids.add(categoryDTO.getCategoryId());
                getIds(categoryDTOList, categoryDTO.getCategoryId(), ids);
            }
        }
        return ids;
    }

    private void update(CategoryDTO categoryDTO) {
        Category category = MallBeanUtil.copy(categoryDTO, Category.class);
        category.setId(categoryDTO.getCategoryId());
        int i = categoryDAO.updateById(category);
    }

    //只获取类目列表，不转树结构
    private List<CategoryDTO> getList() {
        QueryWrapper<Category>queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", CommonConstant.NOT_DEL);
        List<Category> categoryList = categoryDAO.selectList(queryWrapper);
        List<CategoryDTO> categoryDTOList = categoryList.stream().map(category -> toCategoryDTO(category)).collect(Collectors.toList());
        return categoryDTOList;
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


    private CategoryDTO toCategoryDTO(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDTO categoryDTO = MallBeanUtil.copy(category, CategoryDTO.class);
        categoryDTO.setCategoryId(category.getId());
        return categoryDTO;
    }
}
