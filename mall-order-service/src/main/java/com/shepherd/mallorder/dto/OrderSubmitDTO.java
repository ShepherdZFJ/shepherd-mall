package com.shepherd.mallorder.dto;

import lombok.Data;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/23 00:35
 */
@Data
public class OrderSubmitDTO {
    private Long addressId;
    private Integer payType;
    private String token;
    private String payAmount;
    private String remark;
    private Long userId;
}
