package com.shepherd.ware.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/26 14:51
 */
@Data
public class WareInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String areaNo;
    private Integer isDelete;
    private Date createTime;
    private Date updateTime;

}
