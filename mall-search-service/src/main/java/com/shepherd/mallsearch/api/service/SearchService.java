package com.shepherd.mallsearch.api.service;

import com.shepherd.mallsearch.dto.ProductSku;

import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/9 16:03
 */
public interface SearchService {

    List<ProductSku> getSku();
}
