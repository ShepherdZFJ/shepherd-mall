package com.shepherd.mallorder.api.service;

import com.shepherd.mallorder.dto.OrderConfirmDTO;
import com.shepherd.mallorder.dto.OrderDTO;
import com.shepherd.mallorder.dto.OrderSubmitDTO;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/20 19:19
 */
public interface OrderService {
    OrderConfirmDTO settlement(Long userId);

    void submitOrder(OrderSubmitDTO orderSubmit);

    void closeOrder(OrderDTO orderDTO);

    OrderDTO getOrderByOrderNo(String orderNo);

    void updateOrderStatus(String orderNo, Integer status, Integer payType);
}
