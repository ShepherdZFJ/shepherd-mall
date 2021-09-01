package com.shepherd.mall.seckill.api.controller;

import com.shepherd.mall.annotation.ResponseResultBody;
import com.shepherd.mall.seckill.api.service.TestHystrixService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/9/1 10:38
 */
@RestController
@RequestMapping("/test")
@ResponseResultBody
public class TestController {
    @Resource
    private TestHystrixService testHystrixService;

    @GetMapping("/hystrix")
    public String testHystrix() {
        String s = testHystrixService.testFeignWithHystrix(1l);
        return s;
    }
}
