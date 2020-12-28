package com.shepherd.mallbase.api.controller;

import com.shepherd.mall.annotation.ResponseResultBody;
import com.shepherd.mallbase.api.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/12/28 21:47
 */
@RestController
@ResponseResultBody
@RequestMapping("/api/mall/base/sms")
@Api()
public class SmsController {
    @Resource
    private SmsService smsService;
    @GetMapping("/verification/{phoneNumber}")
    @ApiOperation("获取手机验证码")
    public void getCode(@PathVariable("phoneNumber") String phoneNumber) {
        smsService.getSmsCode(phoneNumber);
    }
}
