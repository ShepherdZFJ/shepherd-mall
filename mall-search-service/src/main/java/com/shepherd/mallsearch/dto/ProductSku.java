package com.shepherd.mallsearch.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/9 15:10
 */

/**
 * 1.创建索引
 * 2.创建类型
 * 3.创建文档 ()
 * 4.字段的映射(是否分词 是否索引 是否存储  数据类型是什么 分词器是什么)
 *
 * @author www.itheima.com
 * @version 1.0
 * @Document(indexName = "skuinfo",type = "docs")
 * indexName 指定创建的索引的名称
 * type :指定索引中的类型
 */

@Data
@Document(indexName = "productsku", type = "docs")
public class ProductSku implements Serializable {
    @ApiModelProperty("主键")
    @Id
    private Long id;

    @ApiModelProperty("商品编码")
    private String productNo;

    @ApiModelProperty("商品sku名称")
    @Field(type = FieldType.Text)
    private String name;

    @ApiModelProperty("商品价格")
    private BigDecimal price;

    @ApiModelProperty("商品库存数量")
    private Integer stock;

    @ApiModelProperty("商品sku主图")
    private String mainImage;

    @ApiModelProperty("商品sku子图列表")
    private String subImages;

    @ApiModelProperty("商品重量")
    private Integer weight;

    @ApiModelProperty("商品spu主键")
    private Long productSpuId;

    @ApiModelProperty("所属类目id")
    private Long categoryId;

    @ApiModelProperty("所属类目名称")
    @Field(type = FieldType.Keyword)
    private String categoryName;

    @ApiModelProperty("所属品牌id")
    private Long brandId;

    @ApiModelProperty("所属品牌名称")
    @Field(type = FieldType.Keyword)
    private String brandName;

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

    //规格参数
    private Map<String,Object> specMap;
}
