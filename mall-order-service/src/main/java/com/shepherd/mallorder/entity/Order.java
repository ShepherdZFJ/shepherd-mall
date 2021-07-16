package com.shepherd.mallorder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/16 14:28
 */

@Data
public class Order {
    @TableId(type = IdType.AUTO)
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
    private Date finishTime;
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
    private Integer payStatus;
    private Integer orderStatus;
    private Integer deliveryStatus;
    private Integer isDelete;

}
