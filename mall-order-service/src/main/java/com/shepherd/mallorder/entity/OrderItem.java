package com.shepherd.mallorder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/16 15:09
 */
@Data
public class OrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String orderNo;
    private Long spuId;
    private Long skuId;
    private String name;
    private String image;
    private String spec;
    private BigDecimal price;
    private Integer number;
    private BigDecimal postage;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer isDelete;
    private Integer isReturn;
    private Date createTime;
    private Date updateTime;
}
