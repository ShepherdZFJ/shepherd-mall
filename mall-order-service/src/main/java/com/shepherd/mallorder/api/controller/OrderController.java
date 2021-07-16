package com.shepherd.mallorder.api.controller;

import com.shepherd.mall.vo.LoginVO;
import com.shepherd.mallorder.Utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/16 16:02
 */
@Slf4j
@RestController
@RequestMapping("/api/mall/order")
public class OrderController {

    @GetMapping
    public void test() {
        LoginVO loginVO = UserUtil.currentUser();
        log.info("loginvo: {}", loginVO);
    }
}
