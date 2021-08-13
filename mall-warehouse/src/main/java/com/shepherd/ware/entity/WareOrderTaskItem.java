package com.shepherd.ware.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/11 15:07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WareOrderTaskItem {
    /**
     * id
     * sku_id
     * sku_name
     * sku_num
     * task_id
     * ware_id
     * lock_status
     * create_time
     * update_time
     * is_delete
     */
    private Long id;
    private Long skuId;
    private String skuName;
    private Integer skuNum;
    private Long taskId;
    private Long wareId;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer isDelete;

}
