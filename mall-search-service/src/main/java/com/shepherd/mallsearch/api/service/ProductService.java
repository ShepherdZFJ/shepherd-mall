package com.shepherd.mallsearch.api.service;

import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallsearch.dto.ProductSku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/9 15:47
 */

@FeignClient(name = "${micro-server.mall-product}", path = "/api/mall/product", url="${mall-product-server}")
public interface ProductService {

    @GetMapping("/sku/list")
    ResponseVO getSku();
}
