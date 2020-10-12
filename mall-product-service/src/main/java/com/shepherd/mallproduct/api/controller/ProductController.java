package com.shepherd.mallproduct.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shepherd.mallproduct.api.service.ProductService;
import com.shepherd.mallproduct.dto.ProductDTO;
import com.shepherd.mallproduct.query.ProductQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
