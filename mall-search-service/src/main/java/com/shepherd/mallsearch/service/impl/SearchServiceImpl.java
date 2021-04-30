package com.shepherd.mallsearch.service.impl;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.shepherd.mall.exception.BusinessException;
import com.shepherd.mallsearch.api.service.SearchService;
import com.shepherd.mallsearch.config.ElasticSearchConfig;
import com.shepherd.mallsearch.dto.ProductSku;
import com.shepherd.mallsearch.enums.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/9 16:04
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean addProductToEsBatch(List<ProductSku> productSkuList) {
        if (CollectionUtils.isEmpty(productSkuList)) {
            throw new BusinessException(ErrorCodeEnum.UP_PRODUCT_IS_EMPTY.getCode(), ErrorCodeEnum.UP_PRODUCT_IS_EMPTY.getMessage());
        }
        try {
            BulkRequest bulkRequest = new BulkRequest();
            productSkuList.forEach(productSku -> {
                IndexRequest indexRequest = new IndexRequest("product");
                String productStr = JSON.toJSONString(productSku);
                indexRequest.source(productStr, XContentType.JSON);
                bulkRequest.add(indexRequest);
            });
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
            Boolean result = bulkResponse.hasFailures();

            List<BulkItemResponse> bulkItemResponses = Lists.newArrayList(bulkResponse.getItems());
            if (!CollectionUtils.isEmpty(bulkItemResponses)) {
                List<String> bulkItemIds = bulkItemResponses.stream().map(bulkItemResponse -> bulkItemResponse.getId()).collect(Collectors.toList());
                log.info("商品上架完成：{}", bulkItemIds);
            }
            return !result;
        } catch (Exception e) {
            log.error("商品上架失败：{}", e);
        }
        return false;
    }




}
