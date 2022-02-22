package com.shepherd.mallpay.feign;

import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallpay.dto.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/16 18:19
 */
@FeignClient(name = "${micro-server.mall-order}", path = "/api/mall/order")
public interface OrderService {

    @GetMapping("/orderNo/{orderNo}")
    ResponseVO<Order> getOrderByOrderNo(@PathVariable("orderNo") String orderNo);

    @PutMapping("/status")
    ResponseVO updateOrderStatus(@RequestParam("orderNo") String orderNo, @RequestParam("status") Integer status, @RequestParam("payType") Integer payType);
}

