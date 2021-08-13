package com.shepherd.ware.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/26 10:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WareOrderTask {
    /**
     * id
     * order_id
     * order_no
     * receiver_name
     * receiver_mobile
     * delivery_address
     * order_remark
     * pay_type
     * status
     * description
     * delivery_no
     * create_time
     * update_time
     * ware_id
     * task_remark
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String orderNo;
    private String receiverName;
    private String receiverMobile;
    private String deliveryAddress;
    private String orderRemark;
    private Integer payType;
    private Integer status;
    private String description;
    private String deliveryNo;
    private Date createTime;
    private Date updateTime;
    private Long wareId;
    private String taskRemark;
    private Integer isDelete;
}
