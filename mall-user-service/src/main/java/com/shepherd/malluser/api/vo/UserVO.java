package com.shepherd.malluser.api.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/11/17 23:19
 */
@Data
public class UserVO {
    private String userNo;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private Integer sex;
    private String headPhoto;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;
    private Integer isDelete;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String code;
    private Integer type;//0 -> password  1 -> phone + code
    private Integer firstLogin;
    private Integer count;
    private String ticket;
    private String token;

    private String username;
}
