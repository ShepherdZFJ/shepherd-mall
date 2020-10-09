package com.shepherd.mallpay.service;

import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import com.shepherd.mallpay.api.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/10/8 15:44
 */
@Slf4j
@Service
public class PayServiceImpl implements PayService {
    @Resource
    private BestPayService bestPayService;

    @Override
    public PayResponse create(String orderNo, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum) {

        PayRequest payRequest = new PayRequest();
        payRequest.setOrderName("shepherd-最好的sdk");
        payRequest.setOrderId(orderNo);
        payRequest.setOrderAmount(amount.doubleValue());
        payRequest.setPayTypeEnum(bestPayTypeEnum);
        PayResponse payResponse = bestPayService.pay(payRequest);
        log.info("发起支付 payResponse ={}", payResponse);
        return payResponse;
    }


    @Override
    public String asyncNotify(String notifyData) {
        return null;
    }

    @Override
    public void queryByOrderId(String orderId) {

    }
}
