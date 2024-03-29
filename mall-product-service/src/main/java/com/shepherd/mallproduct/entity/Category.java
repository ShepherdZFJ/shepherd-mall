package com.shepherd.mallproduct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 16:01
 */
@Data
@ApiModel("商品分类")
public class Category implements Serializable {
    @TableId(type= IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("父级类目id")
    private Long parentId;

    @ApiModelProperty("类目名称")
    private String name;

    @ApiModelProperty("类别状态1-正常,0-已废弃")
    private Integer status;

    @ApiModelProperty("排序编号,同类展示顺序,数值相等则自然排序")
    private Integer sortOrder;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("删除标志位")
    private Integer isDelete;
}
