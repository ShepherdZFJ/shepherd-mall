package com.shepherd.mall.vo;


import lombok.Data;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/7/15 22:35
 */
@Data
public class LoginVO {

    private Long userId;
    private String ticket;
    private String token;
    private String userName;
    private String phone;
    private String email;


}