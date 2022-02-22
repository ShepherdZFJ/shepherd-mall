package com.shepherd.mallorder.feign;

import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallorder.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/26 15:50
 */
@FeignClient(name = "${micro-server.mall-warehouse}", path = "/api/mall/ware")
public interface WareService {

    @PostMapping("/sku/stock/decrease")
    ResponseVO decreaseStock(@RequestBody OrderDTO orderDTO);
}
