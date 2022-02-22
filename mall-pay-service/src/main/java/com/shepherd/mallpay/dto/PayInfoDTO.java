package com.shepherd.mallpay.dto;

import com.shepherd.mallpay.entity.PayInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/10/9 20:27
 */
@Data
public class PayInfoDTO extends PayInfo {
    private Long payInfoId;


}
