package com.shepherd.mallorder.api.controller;

import com.shepherd.mall.annotation.ResponseResultBody;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mall.vo.LoginVO;
import com.shepherd.mallorder.api.service.OrderService;
import com.shepherd.mallorder.api.vo.OrderSumbitVO;
import com.shepherd.mallorder.api.vo.OrderVO;
import com.shepherd.mallorder.dto.OrderConfirmDTO;
import com.shepherd.mallorder.dto.OrderDTO;
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
@ResponseResultBody
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
    public OrderConfirmDTO settlement() {
        Long userId = UserUtil.currentUser().getUserId();
        OrderConfirmDTO orderConfirmDTO = orderService.settlement(userId);
        return orderConfirmDTO;

    }

    @PostMapping("/submit")
    public void submitOrder(@RequestBody OrderSumbitVO orderSumbitVO) {
        OrderSubmitDTO orderSubmitDTO = MallBeanUtil.copy(orderSumbitVO, OrderSubmitDTO.class);
        Long userId = UserUtil.currentUser().getUserId();
        orderSubmitDTO.setUserId(userId);
        orderService.submitOrder(orderSubmitDTO);
    }

    @GetMapping("/orderNo/{orderNo}")
    public OrderVO getOrderByOrderNo(@PathVariable("orderNo") String orderNo) {
        OrderDTO orderDTO = orderService.getOrderByOrderNo(orderNo);
        return MallBeanUtil.copy(orderDTO, OrderVO.class);
    }
}
