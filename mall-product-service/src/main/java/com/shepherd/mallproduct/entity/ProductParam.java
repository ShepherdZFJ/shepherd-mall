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
 * @date 2021/2/2 18:22
 */

/**
 * 这个实体类商品参数和商品规格是有区别：
 */
@Data
@ApiModel("商品参数")
public class ProductParam {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("类目id")
    private Long categoryId;

    @ApiModelProperty("商品参数名称")
    private String name;

    @ApiModelProperty("商品参数可选值")
    private String options;

    @ApiModelProperty("删除标志位")
    private String is_delete;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
