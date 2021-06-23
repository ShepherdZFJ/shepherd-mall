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
 * @date 2021/2/2 15:48
 */
@Data
@ApiModel("商品sku")
public class ProductSku {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("商品编码")
    private String productNo;

    @ApiModelProperty("商品sku名称")
    private String name;

    @ApiModelProperty("商品价格")
    private BigDecimal price;

    @ApiModelProperty("商品库存数量")
    private Integer stock;

    @ApiModelProperty("商品sku主图")
    private String mainImage;

    @ApiModelProperty("商品sku子图列表")
    private String subImage;

    @ApiModelProperty("商品重量")
    private Integer weight;

    @ApiModelProperty("商品spu主键")
    private Long spuId;

    @ApiModelProperty("所属类目id")
    private Long categoryId;

    @ApiModelProperty("所属品牌id")
    private Long brandId;

    @ApiModelProperty("商品sku规格")
    private String spec;

    @ApiModelProperty("商品sku销量")
    private Long saleCount;

    @ApiModelProperty("商品sku状态")
    private Integer status;

    @ApiModelProperty("删除标志位")
    private Integer isDelete;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
