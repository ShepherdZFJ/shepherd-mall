package com.shepherd.mallsearch.api.service;

import com.shepherd.mallsearch.dto.ProductSku;

import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/9 16:03
 */
public interface SearchService {
    /**
     * 批量上架商品到es中
     * @param productSkuList
     * @return
     */
    Boolean addProductToEsBatch(List<ProductSku> productSkuList);


}
