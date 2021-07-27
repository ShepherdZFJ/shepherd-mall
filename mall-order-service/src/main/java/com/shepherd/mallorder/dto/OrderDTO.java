package com.shepherd.mallorder.dto;

import com.shepherd.mallorder.entity.Order;
import com.shepherd.mallorder.entity.OrderItem;
import lombok.Data;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/23 16:50
 */
@Data
public class OrderDTO extends Order {
    private List<OrderItem> orderItemList;
    private Long orderId;
}
