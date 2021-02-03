package com.shepherd.mallproduct.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shepherd.mallproduct.dto.ProductDTO;
import com.shepherd.mallproduct.dto.ProductParamDTO;
import com.shepherd.mallproduct.dto.ProductSpecDTO;
import com.shepherd.mallproduct.query.ProductQuery;

import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 16:27
 */
public interface ProductService {
    /**
     * 添加商品
     * @param productDTO
     * @return
     */
    Long addProduct(ProductDTO productDTO);

    /**
     * 批量删除商品
     * @param productSpuIds
     * @return
     */
    Boolean delBatch(List<Long> productSpuIds);

    /**
     *更新商品
     * @param productDTO
     * @return
     */
    Boolean updateProduct(ProductDTO productDTO);

    /**
     * 分页查询商品列表
     * @param query
     * @return
     */
    IPage<ProductDTO> getProductList(ProductQuery query);

    /**
     * 根据id获取商品详情
     * @param productId
     * @return
     */
    ProductDTO getProductDetail(Long productId);

    List<ProductSpecDTO> getProductSpecList(Long categoryId);

    List<ProductParamDTO> getProductParamList(Long categoryId);


}
