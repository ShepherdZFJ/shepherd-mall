package com.shepherd.mallorder.feign;

import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallorder.SkuQuery;
import com.shepherd.mallorder.dto.ProductSku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/5 15:04
 */

@FeignClient(name = "${micro-server.mall-product}", path = "/api/mall/product")
public interface ProductService {

    @GetMapping("/sku/{skuId}")
    ResponseVO<ProductSku> getSku(@PathVariable("skuId") Long skuId);

    @GetMapping("/sku/price")
    ResponseVO<List<ProductSku>> getSkuPrice(SkuQuery query);


}
