package com.shepherd.malluser.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/7/15 22:35
 */
@Data
public class LoginVO {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("ticket，有效时间20s")
    private String ticket;

    @ApiModelProperty("token，有效时间2H")
    private String token;

    private Integer type;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("电话")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;


}