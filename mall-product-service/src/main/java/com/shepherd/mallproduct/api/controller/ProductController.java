package com.shepherd.mallproduct.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mallproduct.api.service.ProductService;
import com.shepherd.mallproduct.api.vo.ProductVO;
import com.shepherd.mallproduct.dto.ProductDTO;
import com.shepherd.mallproduct.query.ProductQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 16:31
 */
@RestController
@RequestMapping("/api/mall/product")
@Api("商品相关接口")
public class ProductController {
    @Resource
    private ProductService productService;

    @GetMapping
    @ApiOperation("获取商品列表")
    public IPage<ProductDTO> getProductList(ProductQuery productQuery){
        IPage<ProductDTO> productDTOList = productService.getProductList(productQuery);
        return productDTOList;
    }

    @PostMapping
    @ApiOperation("添加商品")
    public void addProduct(@RequestBody ProductVO productVO) {
        productService.addProduct(MallBeanUtil.copy(productVO, ProductDTO.class));
    }

    @PutMapping
    @ApiOperation("更新商品")
    public void updateProduct(@RequestBody ProductVO productVO) {
        productService.updateProduct(MallBeanUtil.copy(productVO, ProductDTO.class));
    }

    @DeleteMapping
    @ApiOperation("删除商品(批量)")
    public void delBatch(@RequestBody ProductVO productVO) {
        productService.delBatch(productVO.getProductIds());
    }
}
