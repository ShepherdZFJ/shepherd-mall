package com.shepherd.mall.base;

import lombok.Data;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/7/14 22:23
 */
@Data
public class CasProperties {

    //拦截规则
    private String[] urlPatterns;

    //不拦截规则
    private String[] excludeUrlPatterns;


    //cookie名称
    private String cookieName = "red-book-permission-shepherd";

    /**
     * 缓存清理的间隔时间，单位：秒,默认每分钟清理一次
     */
    private Integer clearInterval = 1;

    /**
     * userForm保活时间，默认2小时
     */
    private Integer userActiveMaxTimie = 120;
}
