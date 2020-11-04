package com.shepherd.mallpay.api.service;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import com.shepherd.mallpay.dto.PayInfoDTO;

import java.math.BigDecimal;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/10/8 15:42
 */
public interface PayService {
    /**
     * 创建/发起支付
     */
    PayResponse create(String orderNo, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum);

    /**
     * 异步通知处理
     * @param notifyData
     */
    String asyncNotify(String notifyData);

    /**
     * 查询支付记录(通过订单号)
     * @param orderId
     * @return
     */
    PayInfoDTO queryByOrderId(String orderId);
}
