package com.shepherd.mallsearch.param;

import com.shepherd.mall.base.BaseQuery;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/6/22 15:07
 */
@Data
public class SearchParam extends BaseQuery {

    /**
     * 页面传递过来的全文匹配关键字
     */
    private String keyword;

    /**
     * 品牌id,可以多选
     */
    private List<Long> brandId;

    /**
     * 三级分类id
     */
    private Long categoryId;

    /**
     * 排序条件：sort=price/salecount/hotscore_desc/asc
     */
    private String sort;

    /**
     * 是否显示有货
     */
    private Integer hasStock;

    /**
     * 价格区间查询
     */
    private String price;

    /**
     * 按照属性进行筛选
     */
    private Map<String, String> specMap;


    /**
     * 原生的所有查询条件
     */
    private String _queryString;


}
