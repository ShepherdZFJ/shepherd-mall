package com.shepherd.mallpay.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/10/9 20:27
 */
@Data
public class PayInfoDTO {

    private Integer userId;

    private String orderNo;

    private Integer payPlatform;

    private String platformNumber;

    private String payStatus;

    private BigDecimal payAmount;
}
