package com.shepherd.mallproduct.api.controller;

import com.shepherd.mallproduct.annotation.Limit;
import com.shepherd.mallproduct.enums.LimitType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2022/4/7 16:12
 */
@RestController
@RequestMapping("/limit")
public class LimiterController {

    private static final AtomicInteger ATOMIC_INTEGER_1 = new AtomicInteger();
    private static final AtomicInteger ATOMIC_INTEGER_2 = new AtomicInteger();
    private static final AtomicInteger ATOMIC_INTEGER_3 = new AtomicInteger();


    @Limit(key = "limitTest", period = 10, count = 3)
    @GetMapping("/test1")
    public int testLimiter1() {

        return ATOMIC_INTEGER_1.incrementAndGet();
    }


    @Limit(key = "customer_limit_test", period = 10, count = 3, limitType = LimitType.CUSTOMER)
    @GetMapping("/test2")
    public int testLimiter2() {

        return ATOMIC_INTEGER_2.incrementAndGet();
    }


    @Limit(key = "ip_limit_test", period = 10, count = 3, limitType = LimitType.IP)
    @GetMapping("/test3")
    public int testLimiter3() {

        return ATOMIC_INTEGER_3.incrementAndGet();
    }

}