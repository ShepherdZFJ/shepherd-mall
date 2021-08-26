package com.shepherd.mall.seckill.feign;

import com.shepherd.mall.seckill.dto.SkuInfo;
import com.shepherd.mall.vo.ResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/26 14:50
 */
@FeignClient(name = "${micro-server.mall-product}", path = "/api/mall/product")
public interface ProductService {

    @GetMapping("/sku/{skuId}")
    ResponseVO<SkuInfo> getSku(@PathVariable("skuId") Long skuId);



}

