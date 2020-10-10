package com.shepherd.mall.exception;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/6/24 14:08
 */
public class BusinessException extends RuntimeException{

    private String code;

    public BusinessException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public BusinessException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

}