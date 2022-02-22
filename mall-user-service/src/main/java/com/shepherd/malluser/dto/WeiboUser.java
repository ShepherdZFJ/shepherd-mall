package com.shepherd.malluser.dto;

import lombok.Data;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/6/28 16:25
 */
@Data
public class WeiboUser {

    private String access_token;

    private String remind_in;

    private long expires_in;

    private String uid;

    private String isRealName;

}
