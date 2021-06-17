package com.shepherd.mallproduct.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.shepherd.mall.annotation.ResponseResultBody;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mallproduct.api.service.CategoryService;
import com.shepherd.mallproduct.api.vo.CategoryVO;
import com.shepherd.mallproduct.dto.CategoryDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/10/11 23:40
 */
@RestController
@ResponseResultBody
@RequestMapping("/api/mall/product/category")
@Api("商品类目相关接口")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    @PostMapping
    @ApiOperation("添加商品类目")
    public void addCategory(@RequestBody CategoryVO categoryVO) {
        categoryService.addCategory(MallBeanUtil.copy(categoryVO, CategoryDTO.class));
    }

    @GetMapping
    @ApiOperation("获取商品类目列表")
    public List<CategoryDTO> getCategoryList() {
        List<CategoryDTO> categoryList = categoryService.getCategoryList();
        return categoryList;
    }


    @PutMapping
    @ApiOperation("更新类目")
    public void updateCategory(@RequestBody CategoryVO categoryVO) {
        CategoryDTO categoryDTO = MallBeanUtil.copy(categoryVO, CategoryDTO.class);
        categoryService.updateCategory(categoryDTO);
    }

    @PutMapping("/batch")
    @ApiOperation("批量更新类目")
    public void updateCategoryBatch(@RequestBody CategoryVO categoryVO) {
        categoryService.updateCategory(categoryVO.getCategoryDTOList());
    }

    @DeleteMapping
    @ApiOperation("删除类目(批量)")
    public void delBatch(@RequestBody CategoryVO categoryVO) {
        categoryService.delBatch(categoryVO.getCategoryIds());
    }

    @GetMapping("/{categoryId}")
    @ApiOperation("获取类目详情")
    public CategoryDTO getCategoryDetail(@PathVariable("categoryId") Long categoryId) {
        return categoryService.getCategoryDetail(categoryId);
    }

    @GetMapping("/test")
    @ApiOperation("test")
    public List<CategoryDTO> test() {
        return categoryService.test();
    }
}
