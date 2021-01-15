package com.shepherd.malladvertise.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/1/14 16:44
 */
@Data
public class Advertise {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("广告类型")
    private Integer type;

    @ApiModelProperty("广告名称")
    private String name;

    @ApiModelProperty("广告地址")
    private String url;

    @ApiModelProperty("广告图片地址")
    private String picture;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("删除标志位")
    private String isDelete;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

}
