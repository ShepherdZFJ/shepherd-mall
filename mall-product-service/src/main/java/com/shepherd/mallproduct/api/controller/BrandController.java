package com.shepherd.mallproduct.api.controller;

import com.shepherd.mall.annotation.ResponseResultBody;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mallproduct.api.service.BrandService;
import com.shepherd.mallproduct.api.vo.BrandVO;
import com.shepherd.mallproduct.dto.BrandDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/2 18:50
 */
@RestController
@ResponseResultBody
@RequestMapping("/api/mall/product/brand")
@Api("品牌相关接口")
public class BrandController {

    @Resource
    private BrandService brandService;

    @PostMapping
    @ApiOperation("添加品牌")
    public void addBrand (@RequestBody BrandVO brandVO) {
        BrandDTO brandDTO = MallBeanUtil.copy(brandVO, BrandDTO.class);
        brandService.addBrand(brandDTO);
    }

    @GetMapping("/category/{categoryId}")
    @ApiOperation("根据分类id查询品牌")
    public List<BrandDTO> getBrandList(@PathVariable("categoryId") Long categoryId) {
        return brandService.getBrandList(categoryId);
    }

    @GetMapping("/{brandId}")
    @ApiOperation("获取品牌详情")
    public BrandDTO getBandDetail(@PathVariable("brandId") Long brandId) {
        return brandService.getBrandDetail(brandId);
    }

    @PutMapping
    @ApiOperation("更新品牌")
    public void updateBrand(@RequestBody BrandVO brandVO) {
        //todo

    }

    @DeleteMapping
    @ApiOperation("删除品牌")
    public void delBatch(@RequestBody BrandVO brandVO) {
        //todo
    }
}
