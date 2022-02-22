package com.shepherd.mallsearch.api.vo;

import lombok.Data;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/6/23 00:45
 */
@Data
public class SpecVO {
    private String specName;
    //这里属性的value值声明为list，为了保证有序，同时把value值去重之后在插入list
    private List<String> specValues;
}
