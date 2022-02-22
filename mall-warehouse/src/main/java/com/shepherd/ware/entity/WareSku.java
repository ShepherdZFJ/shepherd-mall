package com.shepherd.ware.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/26 10:48
 */
@Data
public class WareSku {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long wareId;
    private Long skuId;
    private Integer stock;
    private String skuName;
    private Integer isDelete;
    private Date createTime;
    private Date updateTime;

}
