package com.shepherd.mallsearch.enums;

import lombok.Getter;

/**
 * @author jfWu
 * @version 1.0
 * @date 2019/11/18 11:07
 */
@Getter
public enum ErrorCodeEnum {

    UP_PRODUCT_IS_EMPTY("up product is empty", "上架的商品为空");

    private String code;
    private String message;

    ErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
