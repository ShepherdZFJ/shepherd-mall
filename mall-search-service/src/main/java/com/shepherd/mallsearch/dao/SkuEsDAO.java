package com.shepherd.mallsearch.dao;

import com.shepherd.mallsearch.dto.ProductSku;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/9 15:28
 */
public interface SkuEsDAO extends ElasticsearchRepository<ProductSku, Long> {
}
