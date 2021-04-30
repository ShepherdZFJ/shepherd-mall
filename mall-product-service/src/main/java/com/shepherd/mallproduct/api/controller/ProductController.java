package com.shepherd.mallproduct.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shepherd.mall.annotation.ResponseResultBody;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallproduct.api.service.ProductService;
import com.shepherd.mallproduct.api.vo.ProductVO;
import com.shepherd.mallproduct.dto.ProductDTO;
import com.shepherd.mallproduct.dto.ProductParamDTO;
import com.shepherd.mallproduct.dto.ProductSpecDTO;
import com.shepherd.mallproduct.entity.ProductSku;
import com.shepherd.mallproduct.entity.ProductSpec;
import com.shepherd.mallproduct.query.ProductQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 16:31
 */
@RestController
@ResponseResultBody
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
        productService.delBatch(productVO.getProductSpuIds());
    }

    @GetMapping("/{productSpuId}")
    public ProductDTO getProductDetail(@PathVariable("productSpuId") Long productSpuId) {
        return  productService.getProductDetail(productSpuId);

    }

    @GetMapping("/category/{categoryId}/spec")
    @ApiOperation("根据分类id获取商品规格列表")
    public List<ProductSpecDTO> getProductSpecList(@PathVariable("categoryId") Long categoryId) {
        return productService.getProductSpecList(categoryId);
    }

    @GetMapping("/category/{categoryId}/param")
    @ApiOperation("根据分类id获取商品参数列表")
    public List<ProductParamDTO> getProductParamList(@PathVariable("categoryId") Long categoryId) {
        return productService.getProductParamList(categoryId);
    }

    @PostMapping("/spec")
    @ApiOperation("添加商品规格")
    public void addProductSpec(@RequestBody ProductSpecDTO productSpecDTO) {
        //todo
    }

    @PutMapping("/spec")
    @ApiOperation("更新商品规格")
    public void updateProductSpec(@RequestBody ProductSpecDTO productSpecDTO) {
        //todo
    }

    @DeleteMapping("/spec")
    @ApiOperation("删除商品规格")
    public void delProductSpec(@RequestBody ProductSpecDTO productSpecDTO) {
        //todo
    }

    @PostMapping("/param")
    @ApiOperation("添加商品参数")
    public void addProductParam(@RequestBody ProductParamDTO ProductParamDTO) {
        //todo
    }

    @PutMapping("/param")
    @ApiOperation("更新商品参数")
    public void updateProductParam(@RequestBody ProductParamDTO ProductParamDTO) {
        //todo
    }

    @DeleteMapping("/param")
    @ApiOperation("删除商品参数")
    public void delProductParam(@RequestBody ProductParamDTO ProductParamDTO) {
        //todo
    }

    @GetMapping("/sku/list")
    @ApiOperation("获取商品sku列表")
    public List<ProductSku> getSku() {
        return productService.getProductSku();
    }

    @PutMapping("/{spuId}/up")
    @ApiOperation("获取商品sku列表")
    public ResponseVO upProductSpu(@PathVariable("spuId") Long spuId) {
        productService.upProductSpu(spuId);
        return ResponseVO.success();
    }

}
