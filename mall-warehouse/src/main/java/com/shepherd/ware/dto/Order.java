package com.shepherd.ware.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/26 15:13
 */
@Data
public class Order {
    private Long id;
    private Long userId;
    private String orderNo;
    private Integer totalNum;
    private BigDecimal totalAmount;
    private BigDecimal preAmount;
    private BigDecimal postage;
    private BigDecimal payAmount;
    private Integer payType;
    private Date createTime;
    private Date updateTime;
    private Date payTime;
    private Date deliveryTime;
    private Date endTime;
    private Date closeTime;
    private String deliveryCompanyName;
    private String deliveryNo;
    private String orderRemark;
    private String receiverName;
    private String receiverMobile;
    private String receiverProvince;
    private String receiverCity;
    private String receiverRegion;
    private String receiverDetailAddress;
    private Integer sourceType;
    private String transactionId;
    private Integer status;
    private Integer isDelete;
    private Long orderId;
    List<OrderItem> orderItemList;
}
