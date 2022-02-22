package com.shepherd.mallsearch.api.vo;

import com.shepherd.mallsearch.dto.ProductSku;
import lombok.Data;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/6/23 00:40
 */
@Data
public class SearchResult {

    /**
     * 当前页码
     */
    private Integer pageNo;

    /**
     * 当前每页显示大小
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页码
     */
    private Integer Pages;

    private List<Integer> pageNavs;

    /**
     * 当前查询到的结果，所有涉及到的品牌
     */
    private List<BrandVO> brands;

    /**
     * 当前查询到的结果，所有涉及到的所有属性
     */
    private List<SpecVO> specs;

    /**
     * 当前查询到的结果，所有涉及到的所有分类
     */
    private List<CategoryVO> categoryList;

    /**
     * 查询到的所有商品信息
     */
    private List<ProductSku> product;


    //===========================以上是返回给页面的所有信息============================//


    /* 面包屑导航数据 */
    private List<NavVo> navs;

    @Data
    public static class NavVo {
        private String navName;
        private String navValue;
        private String link;
    }








}
