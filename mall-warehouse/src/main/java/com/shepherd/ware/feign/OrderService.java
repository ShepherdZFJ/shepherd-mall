package com.shepherd.ware.feign;

import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.ware.dto.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/12 16:59
 */
@FeignClient(name = "${micro-server.mall-order}", path = "/api/mall/order")
public interface OrderService {

    @GetMapping("/orderNo/{orderNo}")
    ResponseVO<Order> getOrderByOrderNo(@PathVariable("orderNo") String orderNo);
}
