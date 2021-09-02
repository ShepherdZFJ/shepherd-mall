package com.shepherd.mall.seckill.api.controller;

import com.shepherd.mall.annotation.ResponseResultBody;
import com.shepherd.mall.seckill.api.service.TestSentinelService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/9/2 00:19
 */
@RestController
@RequestMapping("/sentinel")
@ResponseResultBody
@Slf4j
public class TestSentinelController {
    @Resource
    private TestSentinelService testSentinelService;

    @GetMapping("/flow/control/1")
    @ApiOperation("测试限流")
    public String limitFlow() {
        log.info("执行sentinel限流的接口的代码逻辑了");
        String str = " hello, sentinel";
        return str;
    }

    @GetMapping("/degrade")
    @ApiOperation("熔断降级")
    public String degrade() {
        String s = testSentinelService.degrade(1l);
        return s;
    }

    @GetMapping("/custom/resource/try")
    @ApiOperation("使用try抛出异常自定义资源")
    public String customResource() {
         String s = testSentinelService.customResource();
         return s;
    }

    @GetMapping("/custom/resource/annotation")
    @ApiOperation("使用注解自定义资源")
    public String annotationCustomResource() throws InterruptedException {
         String s = testSentinelService.annotationCustomResource();
        return s;

    }



}
