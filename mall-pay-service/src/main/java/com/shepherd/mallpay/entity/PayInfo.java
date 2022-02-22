package com.shepherd.mallpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/10/9 20:21
 */
@Data
public class PayInfo {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("订单编号")
    private String  orderNo;

    private Long orderId;

    @ApiModelProperty("支付流水号")
    private String tradeNo;

    @ApiModelProperty("支付状态")
    private Integer Status;

    @ApiModelProperty("支付金额")
    private BigDecimal payAmount;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("删除标志位")
    private Integer isDelete;

    private String subject;
    private Date confirmTime;
    private Date callbackTime;
    private String callbackContent;
    private Integer type;
}
