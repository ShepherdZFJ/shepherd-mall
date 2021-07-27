package com.shepherd.mallorder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shepherd.mallorder.api.service.OrderItemService;
import com.shepherd.mallorder.dao.OrderItemDAO;
import com.shepherd.mallorder.entity.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/26 19:22
 */
@Service
@Slf4j
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDAO, OrderItem> implements OrderItemService {
}
