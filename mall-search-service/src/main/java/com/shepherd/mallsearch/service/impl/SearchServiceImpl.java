package com.shepherd.mallsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallsearch.api.service.ProductService;
import com.shepherd.mallsearch.api.service.SearchService;
import com.shepherd.mallsearch.dao.SkuEsDAO;
import com.shepherd.mallsearch.dto.ProductSku;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/9 16:04
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    private ProductService productService;
    @Resource
    private SkuEsDAO skuEsDAO;
    @Override
    public List<ProductSku> getSku() {
        ResponseVO responseVO = productService.getSku();
        if (responseVO.getData() != null) {
            List<ProductSku> productSkuList = JSONObject.parseArray(JSON.toJSONString(responseVO.getData()), ProductSku.class);
            skuEsDAO.saveAll(productSkuList);

        }
        return null;
    }
}
