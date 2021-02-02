package com.shepherd.mallproduct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/2 18:14
 */
@Data
@ApiModel("商品规格表")
public class ProductSpec {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("类目id")
    private Long categoryId;

    @ApiModelProperty("商品规格名称")
    private String name;

    @ApiModelProperty("商品规格类型")
    private String options;

    @ApiModelProperty("删除标志位")
    private String is_delete;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
