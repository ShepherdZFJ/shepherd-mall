package com.shepherd.mallorder.api.controller;

import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mall.vo.LoginVO;
import com.shepherd.mallorder.api.service.OrderService;
import com.shepherd.mallorder.api.vo.OrderSumbitVO;
import com.shepherd.mallorder.dto.OrderSubmitDTO;
import com.shepherd.mallorder.utils.UserUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/16 16:02
 */
@Slf4j
@RestController
@RequestMapping("/api/mall/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @GetMapping
    public void test() {
        LoginVO loginVO = UserUtil.currentUser();
        log.info("loginvo: {}", loginVO);
    }

    @GetMapping("/settlement")
    @ApiOperation("去结算")
    public void settlement() {
        Long userId = UserUtil.currentUser().getUserId();
        orderService.settlement(userId);

    }

    @PostMapping("/submit")
    public void submitOrder(@RequestBody OrderSumbitVO orderSumbitVO) {
        OrderSubmitDTO orderSubmitDTO = MallBeanUtil.copy(orderSumbitVO, OrderSubmitDTO.class);
        Long userId = UserUtil.currentUser().getUserId();
        orderSubmitDTO.setUserId(userId);
        orderService.submitOrder(orderSubmitDTO);
    }
}
