package com.shepherd.mall.vo;

import lombok.Data;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/11/20 20:54
 */
@Data
public class ResponseVO<T> {

    private Integer code;

    private String msg;

    private T data;

    public ResponseVO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseVO(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseVO(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
