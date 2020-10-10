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
    private Integer id;

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("订单编号")
    private String  orderNo;

    @ApiModelProperty("支付平台:1-支付宝,2-微信")
    private Integer payPlatform;

    @ApiModelProperty("支付流水号")
    private String platformNumber;

    @ApiModelProperty("支付状态")
    private String payStatus;

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
}
