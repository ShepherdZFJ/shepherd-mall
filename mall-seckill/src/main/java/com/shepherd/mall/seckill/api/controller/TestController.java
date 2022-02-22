package com.shepherd.mall.seckill.api.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.shepherd.mall.annotation.ResponseResultBody;
import com.shepherd.mall.seckill.annotation.RateLimit;
import com.shepherd.mall.seckill.api.service.TestHystrixService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/9/1 10:38
 */
@Slf4j
@RestController
@RequestMapping("/test")
@ResponseResultBody
public class TestController {
    @Resource
    private TestHystrixService testHystrixService;

    /**
     * 限流策略 ：1秒钟2个请求
     */
    private final RateLimiter limiter = RateLimiter.create(2.0);
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/hystrix")
    public String testHystrix() {
        String s = testHystrixService.testFeignWithHystrix(1l);
        return s;
    }



    @GetMapping("/rateLimit")
    public String testLimiter() {
        //500毫秒内，没拿到令牌，就直接进入服务降级
        boolean tryAcquire = limiter.tryAcquire(50, TimeUnit.MILLISECONDS);
        if (!tryAcquire) {
            log.warn("进入服务降级，时间{}", LocalDateTime.now().format(dtf));
            return "当前排队人数较多，请稍后再试！";
        }

        log.info("获取令牌成功，时间{}", LocalDateTime.now().format(dtf));
        return "请求成功";
    }

    @GetMapping("/limit2")
    @RateLimit(key = "limit2", permitsPerSecond = 1, timeout = 50, timeunit = TimeUnit.MILLISECONDS, msg = "当前排队人数较多，请稍后再试！")
    public String limit2() {
        log.info("令牌桶limit2获取令牌成功");
        return "ok";
    }
}
