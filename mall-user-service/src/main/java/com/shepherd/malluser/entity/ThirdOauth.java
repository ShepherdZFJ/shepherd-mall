package com.shepherd.malluser.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/6/29 14:12
 */
@Data
public class ThirdOauth {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long outId;
    private Integer type;
    private String accessToken;
    private Date expireTime;
    private Date createTime;
    private Date updateTime;
}
