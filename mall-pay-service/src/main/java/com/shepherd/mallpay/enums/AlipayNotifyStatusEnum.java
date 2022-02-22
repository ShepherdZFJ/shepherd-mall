package com.shepherd.mallpay.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/19 11:38
 */
@Getter
public enum AlipayNotifyStatusEnum {
    WAIT_BUYER_PAY(0, "等待支付"),
    TRADE_SUCCESS(1, "TRADE_SUCCESS"),
    TRADE_FINISHED(2, "TRADE_FINISHED"),
    TRADE_CLOSED(-1, "TRADE_CLOSED");
    Integer status;
    String desc;

    AlipayNotifyStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static AlipayNotifyStatusEnum getAlipayNotifyStatusEnum(String name) {
        for (AlipayNotifyStatusEnum alipayNotifyStatusEnum : AlipayNotifyStatusEnum.values()) {
            if (Objects.equals(name.toUpperCase(), alipayNotifyStatusEnum.name())) {
                return alipayNotifyStatusEnum;
            }
        }
        return null;
    }

}
