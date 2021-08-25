package com.shepherd.mall.seckill.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/25 23:18
 */
@Data
public class SeckillSession {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private Integer isDelete;
    private Date createTime;
    private Date updateTime;

}
