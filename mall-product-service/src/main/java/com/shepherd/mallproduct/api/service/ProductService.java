package com.shepherd.mallproduct.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shepherd.mallproduct.dto.ProductDTO;
import com.shepherd.mallproduct.query.ProductQuery;

import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 16:27
 */
public interface ProductService {
    Long addProduct(ProductDTO productDTO);
    Boolean delBatch(List<Long> productIds);
    Boolean updateProduct(ProductDTO productDTO);
    IPage<ProductDTO> getProductList(ProductQuery query);


}
