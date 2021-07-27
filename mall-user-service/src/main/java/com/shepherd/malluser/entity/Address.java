package com.shepherd.malluser.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/18 10:56
 */
@Data
public class Address {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String mobile;
    private String postcode;
    private String province;
    private String city;
    private String region;
    private String detailAddress;
    private Integer isDefault;
    private Integer isDelete;
}
