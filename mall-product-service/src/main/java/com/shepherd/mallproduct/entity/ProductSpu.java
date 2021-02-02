package com.shepherd.mallproduct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 15:11
 */
@Data
@ApiModel("商品spu")
public class ProductSpu {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("类目id")
    private Long categoryId;

    @ApiModelProperty("品牌id")
    private Long brandId;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品副标题")
    private String subtitle;

    @ApiModelProperty("商品主图")
    private String mainImage;

    @ApiModelProperty("商品子图")
    private String subImages;

    @ApiModelProperty("商品详情")
    private String detail;

    @ApiModelProperty("规格列表")
    private String specItems;

    @ApiModelProperty("参数列表")
    private String paramItems;

    @ApiModelProperty("商品状态.1-在售 0-下架")
    private Integer status;

    @ApiModelProperty("销量")
    private Long saleCount;

    @ApiModelProperty("删除标志位")
    private Integer isDelete;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

}
