package com.shepherd.mallproduct.feign;

import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallproduct.dto.ProductSkuDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/4/30 14:54
 */

@FeignClient(name = "mall-search-service", path = "/api/mall/search")
public interface SearchService {

    @PostMapping("/product")
    ResponseVO addProductToEs(@RequestBody List<ProductSkuDTO> productSkuDTOs);
}
