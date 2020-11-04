package com.shepherd.mallpay.api.controller;

import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import com.shepherd.mallpay.api.service.PayService;
import com.shepherd.mallpay.dto.PayInfoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/10/8 22:52
 */
@Controller
@RequestMapping("/api/mall/pay")
@Api("支付相关接口")
@Slf4j
public class PayController {
    @Resource
    private PayService payService;
    @Resource
    private WxPayConfig  wxPayConfig;

    @GetMapping("/create")
    @ApiOperation("发起支付")
    public ModelAndView create(@RequestParam("orderNo") String orderNo, @RequestParam("amount") BigDecimal amount,
                               @RequestParam("payType") BestPayTypeEnum bestPayTypeEnum) {
        PayResponse response = payService.create(orderNo, amount, bestPayTypeEnum);

        //支付方式不同，渲染就不同, WXPAY_NATIVE使用codeUrl,  ALIPAY_PC使用body
        Map<String, String> map = new HashMap<>();
        if (bestPayTypeEnum == BestPayTypeEnum.WXPAY_NATIVE) {
            map.put("codeUrl", response.getCodeUrl());
            map.put("orderNo", orderNo);
            map.put("returnUrl", wxPayConfig.getReturnUrl());
            return new ModelAndView("wxView", map);
        }else if (bestPayTypeEnum == BestPayTypeEnum.ALIPAY_PC) {
            map.put("body", response.getBody());
            return new ModelAndView("alipayView", map);
        }

        throw new RuntimeException("暂不支持的支付类型");
    }

    @PostMapping("/notify")
    @ApiOperation("异步通知")
    @ResponseBody
    public String asyncNotify(@RequestBody String notifyData) {
        return payService.asyncNotify(notifyData);
    }

    @GetMapping
    @ApiOperation("根据订单号查询支付记录")
    public PayInfoDTO queryByOrderId(@RequestParam String orderId) {
        log.info("查询支付记录...");
        return payService.queryByOrderId(orderId);
    }






    }
