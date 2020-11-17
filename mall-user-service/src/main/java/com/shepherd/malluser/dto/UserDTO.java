package com.shepherd.malluser.dto;

import com.shepherd.malluser.entity.User;
import lombok.Data;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/11/17 23:19
 */
@Data
public class UserDTO extends User {
    private String code;
    private Integer type;
    private Integer firstLogin;
    private String ticket;
    private String token;
}