package com.shepherd.mall.constant;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 13:33
 */
public interface CommonConstant {

    Integer DEL = 1;

    Integer NOT_DEL = 0;

    Integer DEFAULT_PAGE_SIZE = 2000;

    Integer DEFAULT_PAGE_NO = 1;

    Integer PRODUCT_ON_SALE = 1;

    Integer PRODUCT_SALE_OUT = 0;

    Integer CATEGORY_AVAILABLE = 1;

    Integer CATEGORY_ABANDON = 0;

    /**
     * 本机号码一键登录
     */
    Integer PHONE_LOCAL_LOGIN = 1;
    /**
     * 短信验证登录
     */
    Integer PHONE_MESSAGE_LOGIN = 2;
    /**
     * 账户密码登录
     */
    Integer USER_PASSWORD_LOGIN = 3;

    Integer FIRST_LOGIN = 1;

    Integer NOT_FIRST_LOGIN = 0;

    Long DEFAULT_SALE_COUNT = 0L;

}
